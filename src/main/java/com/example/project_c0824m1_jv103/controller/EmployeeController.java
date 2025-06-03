package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.dto.EmployeeDto;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.service.employee.EmployeeService;
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
        return "add-employee-form";
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
        model.addAttribute("employeeDto", employeeDto);
        model.addAttribute("roles", filteredRoles);
        return "edit-employee-form";
    }


    @PostMapping("create")
    public String createEmployee(@ModelAttribute("employee") Employee employee) {
        employeeService.save(employee);
        return "redirect:/employees";
    }

    // Test (Phần của anh hiển)
    @GetMapping("")
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

        model.addAttribute("employees", employees);
        return "list";
    }

    @PostMapping("edit-employee")
    public String editEmployee(@ModelAttribute("employeeDto") EmployeeDto employeeDto,
//                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            return "edit-employee-form";
//        }
//        if (EncryptPasswordUtils.CheckPassword(userDto.getOldPassword(), user.getPassword())) {
//            if (userDto.getPassword().equals(userDto.getConfirmPassword())) {
//                user.setPassword(EncryptPasswordUtils.EncryptPasswordUtils(userDto.getPassword()));
//                usersService.saveUser(user);
//                redirectAttributes.addFlashAttribute("message", "Thay đổi mật khẩu thành công");
//                return "redirect:/user/profile";
//            } else {
//                redirectAttributes.addFlashAttribute("message", "Vui lòng nhập lại mật khẩu mới cho trùng khớp!");
//                return "redirect:/user/change-password-page";
//            }
//        } else {
//            redirectAttributes.addFlashAttribute("message", "Mật khẩu hiện tại không đúng!");
//            return "redirect:/user/change-password-page";
//        }
        if(employeeDto.getPasswordConfirm().equals(employeeDto.getPassword())) {
            Employee employee = employeeService.findById(employeeDto.getEmployeeId());
            BeanUtils.copyProperties(employeeDto, employee);
            employeeService.save(employee);
            redirectAttributes.addFlashAttribute("message", "Thay đổi thành công!");
            return "redirect:/employees";
        } else {
            redirectAttributes.addFlashAttribute("message", "Vui lòng nhập lại mật khẩu cho khớp!");
            return "redirect:/employees/show-edit-employee/" + employeeDto.getEmployeeId();
        }
    }
}
