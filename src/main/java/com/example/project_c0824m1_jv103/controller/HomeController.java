package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.common.EncryptPasswordUtils;
import com.example.project_c0824m1_jv103.dto.EmployeePersonalDto;
import com.example.project_c0824m1_jv103.dto.EmployeePersonalPasswordDto;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.service.employee.IEmployeeService;
import com.example.project_c0824m1_jv103.service.security.UserDetailsServiceImpl;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    IEmployeeService employeeService;

    @GetMapping("")
    public String showHome(){
        return "homePage/homePage";
    }

    @GetMapping("personal-info")
    public String showPersonalInfo(Model model,
                                   @AuthenticationPrincipal UserDetails userDetails){
        EmployeePersonalDto dto = new EmployeePersonalDto();
        Employee employee = employeeService.findByEmail(userDetails.getUsername());
        BeanUtils.copyProperties(employee,dto);
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
                                     @AuthenticationPrincipal UserDetails userDetails) {

        Employee currentEmployee = employeeService.findByEmail(userDetails.getUsername());

        Employee emailCheck = employeeService.findByEmail(dto.getEmail());
        if (emailCheck != null && !emailCheck.getEmployeeId().equals(currentEmployee.getEmployeeId())) {
            bindingResult.rejectValue("email", "error.employee", "Email đã tồn tại.");
        }

        Employee phoneCheck = employeeService.findByPhone(dto.getPhone());
        if (phoneCheck != null && !phoneCheck.getEmployeeId().equals(currentEmployee.getEmployeeId())) {
            bindingResult.rejectValue("phone", "error.employee", "Số điện thoại đã tồn tại.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEditing", true);
            model.addAttribute("hasError", true);
            return "homePage/personal_info";
        }

        try {
            String oldEmail = currentEmployee.getEmail();
            employeeService.updateEmployeeInfo(dto);
            if (!dto.getEmail().equalsIgnoreCase(oldEmail)) {
                SecurityContextHolder.clearContext();
                return "redirect:/login";
            }
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isEditing", true);
            model.addAttribute("hasError", true);
            return "homePage/personal_info";
        }
        model.addAttribute("message", "Cập nhật thông tin thành công.");
        return "redirect:/personal-info";
    }


//    @PostMapping("/personal-info/change-password")
//    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
//                                 @ModelAttribute("passwordDto") @Valid EmployeePersonalPasswordDto passwordDto,
//                                 BindingResult bindingResult,
//                                 RedirectAttributes redirectAttributes) {
//
//        Employee employee = employeeService.findByEmail(userDetails.getUsername());
//
//        if (!EncryptPasswordUtils.ParseEncrypt(passwordDto.getOldPassword(), employee.getPassword())) {
//            bindingResult.rejectValue("oldPassword", "error.passwordDto", "Mật khẩu hiện tại không đúng.");
//        }
//
//        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
//            bindingResult.rejectValue("confirmPassword", "error.passwordDto", "Mật khẩu xác nhận không khớp.");
//        }
//
//        if (bindingResult.hasErrors()) {
//            redirectAttributes.addFlashAttribute("passwordError", true);
//            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.passwordDto", bindingResult);
//            redirectAttributes.addFlashAttribute("passwordDto", passwordDto);
//            return "redirect:/personal-info";
//        }
//
//        String encodedNewPassword = EncryptPasswordUtils.EncryptPasswordUtils(passwordDto.getNewPassword());
//        employee.setPassword(encodedNewPassword);
//
//        employeeService.save(employee);
//
//        redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công!");
//        return "redirect:/personal-info";
//    }

    @PostMapping("/personal-info/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @ModelAttribute("passwordDto") @Valid EmployeePersonalPasswordDto passwordDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        Employee employee = employeeService.findByEmail(userDetails.getUsername());

        if (!passwordDto.getOldPassword().equals(employee.getPassword())) {
            bindingResult.rejectValue("oldPassword", "error.passwordDto", "Mật khẩu hiện tại không đúng.");
        }

        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.passwordDto", "Mật khẩu xác nhận không khớp.");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("passwordError", true);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.passwordDto", bindingResult);
            redirectAttributes.addFlashAttribute("passwordDto", passwordDto);
            return "redirect:/personal-info";
        }

        employee.setPassword(passwordDto.getNewPassword());
        employeeService.save(employee);

        redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công!");
        return "redirect:/personal-info";
    }

}
