package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.dto.EmployeePersonalDto;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.service.employee.IEmployeeService;
import com.example.project_c0824m1_jv103.service.security.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

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
        model.addAttribute("employee", employee);
        return "homePage/personal_info";
    }

    @PostMapping("/personal-info-update")
    public String updatePersonalInfo(@Valid @ModelAttribute("employee") EmployeePersonalDto dto,
                                     BindingResult bindingResult,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            return "homePage/personal_info";
        }

        try {
            employeeService.updateEmployeeInfo(dto);
            model.addAttribute("message", "Cập nhật thông tin thành công.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "homePage/personal_info";
        }

        return "redirect:/personal-info";
    }
}
