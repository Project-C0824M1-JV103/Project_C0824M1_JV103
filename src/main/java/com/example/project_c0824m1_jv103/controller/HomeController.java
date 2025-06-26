package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.common.EncryptPasswordUtils;
import com.example.project_c0824m1_jv103.dto.EmployeePersonalDto;
import com.example.project_c0824m1_jv103.dto.EmployeePersonalPasswordDto;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.service.EmailService;
import com.example.project_c0824m1_jv103.service.employee.IEmployeeService;
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
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    IEmployeeService employeeService;

    @Autowired
    private EmailService emailService;

    @GetMapping("")
    public String showHome() {
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

        Employee emailCheck = employeeService.findByEmail(dto.getEmail());
        if (emailCheck != null && !emailCheck.getEmployeeId().equals(currentEmployee.getEmployeeId())) {
            bindingResult.rejectValue("email", "error.employee", "Email đã được sử dụng.");
        }

        Employee phoneCheck = employeeService.findByPhone(dto.getPhone());
        if (phoneCheck != null && !phoneCheck.getEmployeeId().equals(currentEmployee.getEmployeeId())) {
            bindingResult.rejectValue("phone", "error.employee", "Số điện thoại đã được sử dụng.");
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
            bindingResult.rejectValue("newPassword", "error.password", "Mật khẩu mới không được giống với mật khẩu cũ!");
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

    @PostMapping("/personal-info/send-otp")
    @ResponseBody
    public Map<String, Object> sendOtp(@RequestParam String email) {
        Map<String, Object> res = new HashMap<>();
        boolean sent = emailService.sendOtp(email);
        res.put("success", sent);
        res.put("message", sent ? "Đã gửi OTP đến email." : "Không thể gửi OTP. Vui lòng thử lại.");
        return res;
    }

    @PostMapping("/personal-info/verify-otp")
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
