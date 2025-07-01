package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.model.Customer;
import com.example.project_c0824m1_jv103.service.customer.ICustomerService;
import com.example.project_c0824m1_jv103.service.sale.ISaleService;
import com.example.project_c0824m1_jv103.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/Customer")
public class CustomerController extends BaseAdminController  {
    @Autowired
    private ICustomerService iCustomerService;
    
    @Autowired
    private ISaleService saleService;
    
    @Autowired
    private EmailService emailService;

    @GetMapping()
    public String showListCustomer(Model model,
                                   Principal principal,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "6") int size,
                                   @RequestParam(required = false) String customerName,
                                   @RequestParam(required = false) String phoneNumber) {

        if (page < 0) page = 0;
        if (size <= 0) size = 6;

        Pageable pageable = PageRequest.of(page, size, Sort.by("customerId").descending());
        Page<Customer> customerPage;
        boolean isSearch = false;

        String trimmedName = StringUtils.hasText(customerName) ? customerName.trim() : null;
        String trimmedPhone = StringUtils.hasText(phoneNumber) ? phoneNumber.trim() : null;

        if (trimmedName != null || trimmedPhone != null) {
            // Perform search using Java backend
            customerPage = iCustomerService.searchByNameAndPhone(trimmedName, trimmedPhone, pageable);
            isSearch = true;



        } else {

            customerPage = iCustomerService.findAll(pageable);
        }

        // Add model attributes
        model.addAttribute("customerPage", customerPage);
        model.addAttribute("customers", customerPage.getContent());
        model.addAttribute("currentPage", "customer");
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("totalElements", customerPage.getTotalElements());
        model.addAttribute("pageNumber", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("customerName", trimmedName);
        model.addAttribute("phoneNumber", trimmedPhone);
        model.addAttribute("isSearch", isSearch);

        // Add search statistics
        if (isSearch) {
            model.addAttribute("searchResultsCount", customerPage.getTotalElements());
            model.addAttribute("hasResults", customerPage.getTotalElements() > 0);
        }

        return "admin/showListCustomer";
    }

    @GetMapping("/edit/{id}")
    public String showEditCustomerForm(@PathVariable("id") Integer id, Model model, Principal principal) {
        Optional<Customer> customer = iCustomerService.findById(id);
        if (customer.isPresent()) {
            Customer customerObj = customer.get();
            
            // Đếm số đơn hàng của customer từ database
            Long totalOrders = saleService.countSalesByCustomerId(id);
            
            model.addAttribute("customer", customerObj);
            model.addAttribute("totalOrders", totalOrders);
            model.addAttribute("currentPage", "customer");
            return "admin/editCustomer";
        } else {
            return "redirect:/Customer?error=notfound";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateCustomer(@PathVariable("id") Integer id,
                                 @Valid @ModelAttribute Customer customer,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 Principal principal) {
        try {
            // Set customer ID first to ensure proper binding
            customer.setCustomerId(id);
            
            // Check for validation errors first  
            if (bindingResult.hasErrors()) {
                // Đếm số đơn hàng của customer để hiển thị
                Long totalOrders = saleService.countSalesByCustomerId(id);
                model.addAttribute("totalOrders", totalOrders);
                model.addAttribute("customer", customer);
                model.addAttribute("currentPage", "customer");
                return "admin/editCustomer";
            }
            
            // Variable to track if there are custom validation errors
            boolean hasCustomErrors = false;
            
            // Validation for duplicate email across all tables (Customer, Employee, Supplier)
            if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()) {
                if (iCustomerService.isEmailExistsForOtherCustomer(customer.getEmail(), id)) {
                    // Thêm lỗi trực tiếp vào BindingResult để hiển thị cạnh field
                    bindingResult.rejectValue("email", "duplicate.email", "Email này đã được sử dụng trong hệ thống!");
                    hasCustomErrors = true;
                }
            }

            // Validation for duplicate phone number
            if (customer.getPhoneNumber() != null && !customer.getPhoneNumber().trim().isEmpty()) {
                if (iCustomerService.isPhoneExistsForOtherCustomer(customer.getPhoneNumber(), id)) {
                    // Thêm lỗi trực tiếp vào BindingResult để hiển thị cạnh field
                    bindingResult.rejectValue("phoneNumber", "duplicate.phone", "Số điện thoại này đã được sử dụng!");
                    hasCustomErrors = true;
                }
            }
            
            // If there are custom validation errors, return to form
            if (hasCustomErrors) {
                // Đếm số đơn hàng của customer để hiển thị
                Long totalOrders = saleService.countSalesByCustomerId(id);
                model.addAttribute("totalOrders", totalOrders);
                model.addAttribute("customer", customer);
                model.addAttribute("currentPage", "customer");
                return "admin/editCustomer";
            }

            // All validations passed, save the customer
            iCustomerService.save(customer);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin khách hàng thành công!");
        } catch (Exception e) {
            e.printStackTrace(); // For debugging
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật thông tin khách hàng: " + e.getMessage());
        }
        return "redirect:/Customer";
    }

    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            iCustomerService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa khách hàng này!");
        }
        return "redirect:/Customer";
    }

    // API endpoints for email verification
    @PostMapping("/send-otp")
    @ResponseBody
    public Map<String, Object> sendOtp(@RequestParam String email) {
        Map<String, Object> res = new HashMap<>();
        boolean sent = emailService.sendOtp(email);
        res.put("success", sent);
        res.put("message", sent ? "Đã gửi OTP đến email." : "Không thể gửi OTP. Vui lòng thử lại.");
        return res;
    }

    @PostMapping("/verify-otp")
    @ResponseBody
    public Map<String, Object> verifyOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");
        boolean verified = emailService.verifyOtp(email, otp);
        Map<String, Object> res = new HashMap<>();
        res.put("verified", verified);
        res.put("message", verified ? "Xác thực thành công!" : "Mã OTP không đúng hoặc đã hết hạn.");
        return res;
    }
}
