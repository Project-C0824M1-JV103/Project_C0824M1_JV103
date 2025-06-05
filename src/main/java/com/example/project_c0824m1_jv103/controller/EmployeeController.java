package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.common.EncryptPasswordUtils;
import com.example.project_c0824m1_jv103.dto.EmployeeDto;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.service.employee.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("create")
    public String showCreateForm(Model model) {
        List<Employee.Role> allRoles = Arrays.asList(Employee.Role.values());

        List<Employee.Role> filteredRoles = allRoles.stream()
                .filter(role -> role != Employee.Role.Admin)
                .toList();

        model.addAttribute("employee", new Employee());
        model.addAttribute("roles", filteredRoles);
        model.addAttribute("statuses", Employee.Status.values());
        return "employee/add-employee-form";
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
        return "redirect:/employees/list";
    }

    @GetMapping("show-edit-employee/{id}")
    public String showEditEmployeeForm(@PathVariable Integer id, Model model) {
        List<Employee.Role> allRoles = Arrays.asList(Employee.Role.values());

        List<Employee.Role> filteredRoles = allRoles.stream()
                .filter(role -> role != Employee.Role.Admin)
                .toList();
        Employee employee = employeeService.findById(id);
        EmployeeDto employeeDto = new EmployeeDto();
        BeanUtils.copyProperties(employee, employeeDto);
        employeeDto.setRole(employee.getRole().toString());
        model.addAttribute("employeeDto", employeeDto);
        model.addAttribute("roles", filteredRoles);
        return "employee/edit-employee-form";
    }


    @PostMapping("create")
    public String createEmployee(@ModelAttribute("employee") Employee employee) {
        employeeService.save(employee);
        return "redirect:/employees/list";
    }

    // Test (Phần của anh hiển)
    @GetMapping("/list")
    public String listEmployees(Model model,
                                @RequestParam(value = "fullName", required = false) String fullName,
                                @RequestParam(value = "phone", required = false) String phone,
                                @RequestParam(value = "role", required = false) String role) {
        List<String> roles = Arrays.stream(Employee.Role.values())
                .map(Enum::name)
                .toList();
        model.addAttribute("roles", roles);

        List<Employee> employees;
        if ((fullName == null || fullName.isEmpty()) &&
                (phone == null || phone.isEmpty()) &&
                (role == null || role.isEmpty())) {
            employees = employeeService.findAll();
        } else {
            employees = employeeService.searchEmployees(fullName, phone, role);
        }

        model.addAttribute("listEmployee", employees);
        return "/employee/list_employee";
    }

    // Add main listing endpoint
    @GetMapping({"", "/"})
    public String mainListEmployees(Model model,
                                    @RequestParam(value = "fullName", required = false) String fullName,
                                    @RequestParam(value = "phone", required = false) String phone,
                                    @RequestParam(value = "role", required = false) String role) {
        return listEmployees(model, fullName, phone, role);
    }

    @PostMapping("edit-employee")
    public String editEmployee(@Valid @ModelAttribute("employeeDto") EmployeeDto employeeDto, BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<Employee.Role> allRoles = Arrays.asList(Employee.Role.values());

            List<Employee.Role> filteredRoles = allRoles.stream()
                    .filter(role -> role != Employee.Role.Admin)
                    .toList();
            model.addAttribute("roles", filteredRoles);
            return "employee/edit-employee-form";
        }

        Employee employee = employeeService.findById(employeeDto.getEmployeeId());
        BeanUtils.copyProperties(employeeDto, employee);
        employee.setRole(Employee.Role.valueOf(employeeDto.getRole()));
        employeeService.save(employee);
        redirectAttributes.addFlashAttribute("successMessage", "Thay đổi thông tin nhân viên " + employee.getFullName() + " thành công!");
        return "redirect:/employees/list";
    }
}
