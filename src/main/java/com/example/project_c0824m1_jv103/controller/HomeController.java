package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.common.EncryptPasswordUtils;
import com.example.project_c0824m1_jv103.dto.EmployeePersonalDto;
import com.example.project_c0824m1_jv103.dto.EmployeePersonalPasswordDto;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.service.EmailService;
import com.example.project_c0824m1_jv103.service.employee.IEmployeeService;
import com.example.project_c0824m1_jv103.service.security.UserDetailsServiceImpl;
import com.example.project_c0824m1_jv103.model.Product;
import com.example.project_c0824m1_jv103.service.product.IProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    IEmployeeService employeeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private IProductService productService;

    @GetMapping
    public String home(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.findActiveProducts(pageable);
        model.addAttribute("productPage", productPage);

        int totalPages = productPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "homePage/homePage";
    }

    @GetMapping("personal-info")
    public String showPersonalInfo(Model model,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        EmployeePersonalDto dto = new EmployeePersonalDto();
        Employee employee = employeeService.findByEmail(userDetails.getUsername());
        BeanUtils.copyProperties(employee, dto);
        model.addAttribute("employee", dto);
        if (!model.containsAttribute("passwordDto")) {
            model.addAttribute("passwordDto", new EmployeePersonalPasswordDto());
        }

        model.addAttribute("isEditing", false);
        return "homePage/personal_info";
    }

    @PostMapping("/personal-info-update")
    public String updatePersonalInfo(@Valid @ModelAttribute("employee") EmployeePersonalDto dto,
                                     BindingResult bindingResult,
                                     Model model,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes redirectAttributes,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {

        Employee currentEmployee = employeeService.findByEmail(userDetails.getUsername());

        if (!dto.getEmail().equalsIgnoreCase(currentEmployee.getEmail())) {
            if (employeeService.isEmailExistsForOtherEmployee(dto.getEmail(), currentEmployee.getEmployeeId())) {
                bindingResult.rejectValue("email", "error.employee", "Email đã được sử dụng.");
            }
        }

        if (!dto.getPhone().equals(currentEmployee.getPhone())) {
            if (employeeService.isPhoneExistsForOtherEmployee(dto.getPhone(), currentEmployee.getEmployeeId())) {
                bindingResult.rejectValue("phone", "error.employee", "Số điện thoại đã được sử dụng.");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEditing", true);
            model.addAttribute("hasError", true);
            model.addAttribute("employee", dto);
            model.addAttribute("passwordDto", new EmployeePersonalPasswordDto());
            return "homePage/personal_info";
        }

        try {
            String oldEmail = currentEmployee.getEmail();
            employeeService.updateEmployeeInfo(dto);
            if (!dto.getEmail().equalsIgnoreCase(oldEmail)) {
                SecurityContextHolder.clearContext();
                new SecurityContextLogoutHandler().logout(request, response, null);
                redirectAttributes.addFlashAttribute("passwordMessage", "Email đã thay đổi. Vui lòng đăng nhập lại.");
                redirectAttributes.addFlashAttribute("messageType", "info");
                return "redirect:/login";
            }
        } catch (RuntimeException e) {
            model.addAttribute("personalInfoMessage", e.getMessage());
            model.addAttribute("messageType", "danger");
            model.addAttribute("isEditing", true);
            model.addAttribute("hasError", true);
            model.addAttribute("employee", dto);
            model.addAttribute("passwordDto", new EmployeePersonalPasswordDto());
            return "homePage/personal_info";
        }
        redirectAttributes.addFlashAttribute("personalInfoMessage", "Cập nhật thông tin thành công.");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/personal-info";
    }

    @PostMapping("/personal-info/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @Valid @ModelAttribute("passwordDto") EmployeePersonalPasswordDto passwordDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        Employee employee = employeeService.findByEmail(userDetails.getUsername());
        EmployeePersonalDto employeeDto = new EmployeePersonalDto();
        BeanUtils.copyProperties(employee, employeeDto);
        model.addAttribute("employee", employeeDto);
        model.addAttribute("isEditing", false);

        if (bindingResult.hasErrors()) {
            model.addAttribute("hasError", true);
            model.addAttribute("passwordDto", passwordDto);
            return "homePage/personal_info";
        }

        if (!EncryptPasswordUtils.ParseEncrypt(passwordDto.getOldPassword(), employee.getPassword())) {
            bindingResult.rejectValue("oldPassword", "error.password", "Mật khẩu hiện tại không đúng!");
            model.addAttribute("hasError", true);
            model.addAttribute("passwordDto", passwordDto);
            return "homePage/personal_info";
        }

        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.password", "Mật khẩu xác nhận không khớp!");
            model.addAttribute("hasError", true);
            model.addAttribute("passwordDto", passwordDto);
            return "homePage/personal_info";
        }

        if (passwordDto.getOldPassword().equals(passwordDto.getNewPassword()) &&
            passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
            bindingResult.rejectValue("newPassword", "error.password", "Mật khẩu mới không được trùng với mật khẩu cũ!");
            model.addAttribute("hasError", true);
            model.addAttribute("passwordDto", passwordDto);
            return "homePage/personal_info";
        }

        String encodedNewPassword = EncryptPasswordUtils.EncryptPasswordUtils(passwordDto.getNewPassword());
        employee.setPassword(encodedNewPassword);
        employeeService.save(employee);
        new SecurityContextLogoutHandler().logout(request, response, null);
        redirectAttributes.addFlashAttribute("passwordMessage", "Đổi mật khẩu thành công!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/login";
    }

    // --- API gửi OTP xác thực email ---
    @PostMapping("/personal-info/send-otp")
    @ResponseBody
    public Map<String, Object> sendOtp(@RequestParam String email) {
        Map<String, Object> res = new HashMap<>();
        boolean sent = emailService.sendOtp(email);
        res.put("success", sent);
        res.put("message", sent ? "Đã gửi OTP đến email." : "Không thể gửi OTP. Vui lòng thử lại.");
        return res;
    }

    // --- API xác thực OTP ---
    @PostMapping("/personal-info/verify-otp")
    @ResponseBody
    public Map<String, Object> verifyOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");
        boolean verified = emailService.verifyOtp(email, otp);
        Map<String, Object> res = new java.util.HashMap<>();
        res.put("verified", verified);
        res.put("message", verified ? "Xác thực thành công!" : "Mã OTP không đúng hoặc đã hết hạn.");
        return res;
    }

    // --- Product Detail Page ---
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.findProductById(id);
            if (product == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Sản phẩm không tồn tại!");
                return "redirect:/";
            }
            
            // Add product to model
            model.addAttribute("product", product);
            
            // Find product variants (same model but different storage)
            List<Product> productVariants = productService.findProductVariants(product);
            model.addAttribute("productVariants", productVariants);
            
            // Add related products from same category (optional)
            if (product.getCategory() != null) {
                Pageable pageable = PageRequest.of(0, 4);
                Page<Product> relatedProducts = productService.findActiveProducts(pageable);
                // Filter out current product and same category
                List<Product> filteredRelated = relatedProducts.getContent().stream()
                    .filter(p -> !p.getProductId().equals(product.getProductId()) && 
                                p.getCategory() != null &&
                                p.getCategory().getCategoryId().equals(product.getCategory().getCategoryId()))
                    .limit(4)
                    .collect(Collectors.toList());
                model.addAttribute("relatedProducts", filteredRelated);
            }
            
            return "homePage/productDetail";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi tải thông tin sản phẩm!");
            return "redirect:/";
        }
    }
    

    @GetMapping("/api/product/{id}/info")
    @ResponseBody
    public Map<String, Object> getProductInfo(@PathVariable("id") Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Product product = productService.findProductById(id);
            if (product == null) {
                response.put("success", false);
                response.put("message", "Sản phẩm không tồn tại");
                return response;
            }
            
            response.put("success", true);
            response.put("productId", product.getProductId());
            response.put("productName", product.getProductName());
            response.put("price", product.getPrice());
            response.put("quantity", product.getQuantity());
            response.put("memory", product.getMemory());
            
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra");
            return response;
        }
    }
    

}
