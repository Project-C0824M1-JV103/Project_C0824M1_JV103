package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.service.employee.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/Employee")
public class EmployeeController {
    @Autowired
    private IEmployeeService employeeService;
    
    @GetMapping("/list_employee")
    public String listEmployee(Model model) {
        model.addAttribute("listEmployee", employeeService.findAllEmployee());
        return "/employee/list_employee";
    }
    
    @PostMapping("/delete")
    public String deleteEmployees(@RequestParam("employeeIds") List<Integer> employeeIds, 
                                 RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployeesByIds(employeeIds);
            if (employeeIds.size() == 1) {
                redirectAttributes.addFlashAttribute("successMessage", "Đã xóa thành công 1 nhân viên!");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Đã xóa thành công " + employeeIds.size() + " nhân viên!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa nhân viên: " + e.getMessage());
        }
        return "redirect:/Employee/list_employee";
    }
}
