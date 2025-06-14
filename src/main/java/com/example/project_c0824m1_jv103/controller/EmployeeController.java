package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.EmployeeCreateDto;
import com.example.project_c0824m1_jv103.dto.EmployeeEditDto;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.service.employee.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController extends BaseAdminController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/create")
    public String showCreateForm(Model model, Principal principal) {
        List<Employee.Role> allRoles = Arrays.asList(Employee.Role.values());

        List<Employee.Role> filteredRoles = allRoles.stream()
                .filter(role -> role != Employee.Role.Admin)
                .toList();

        model.addAttribute("employee", new Employee());
        model.addAttribute("roles", filteredRoles);
        model.addAttribute("statuses", Employee.Status.values());
        model.addAttribute("currentPage", "employee");
        return "employee/add-employee-form";
    }

    @PostMapping("/delete")
    public String deleteEmployees(@RequestParam("employeeIds") List<Integer> employeeIds,
                                  RedirectAttributes redirectAttributes,
                                  Principal principal) {
        try {
            employeeService.deleteEmployeesByIds(employeeIds);
            if (employeeIds.size() == 1) {
                redirectAttributes.addFlashAttribute("successMessage", "Đã vô hiệu hóa thành công 1 nhân viên!");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Đã vô hiệu hóa thành công " + employeeIds.size() + " nhân viên!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi vô hiệu hóa nhân viên: " + e.getMessage());
        }
        return "redirect:/employees";
    }

    @GetMapping("/show-edit-employee/{id}")
    public String showEditEmployeeForm(@PathVariable Integer id, Model model, Principal principal) {
        List<Employee.Role> allRoles = Arrays.asList(Employee.Role.values());

        List<Employee.Role> filteredRoles = allRoles.stream()
                .filter(role -> role != Employee.Role.Admin)
                .toList();
        Employee employee = employeeService.findById(id);
        EmployeeCreateDto employeeDto = new EmployeeCreateDto();
        BeanUtils.copyProperties(employee, employeeDto);
        employeeDto.setRole(employee.getRole().toString());
        model.addAttribute("employeeDto", employeeDto);
        model.addAttribute("roles", filteredRoles);
        model.addAttribute("currentPage", "employee");
        return "employee/edit-employee-form";
    }

    @PostMapping("/create")
    public String createEmployee(@Valid @ModelAttribute("employee") EmployeeCreateDto employeeDto,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes,
                                 Principal principal) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Employee.Role.values());
            model.addAttribute("currentPage", "employee");
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin nhập vào!");
            return "employee/add-employee-form";
        }
        // Kiểm tra trùng email
        if (employeeService.findByEmail(employeeDto.getEmail()) != null) {
            model.addAttribute("errorMessage", "Email đã tồn tại, vui lòng nhập email khác!");
            model.addAttribute("roles", Employee.Role.values());
            model.addAttribute("currentPage", "employee");
            return "employee/add-employee-form";
        }
        Employee employee = new Employee();
        org.springframework.beans.BeanUtils.copyProperties(employeeDto, employee);
        employee.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
        if (employeeDto.getRole() != null) {
            employee.setRole(Employee.Role.valueOf(employeeDto.getRole()));
        }
        
        try {

            employeeService.save(employee);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm nhân viên " + employee.getFullName() + " thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi thêm nhân viên: " + e.getMessage());
        }

        return "redirect:/employees";
    }

    @GetMapping()
    public String listEmployees(Model model,
                                @RequestParam(value = "fullName", required = false) String fullName,
                                @RequestParam(value = "phone", required = false) String phone,
                                @RequestParam(value = "role", required = false) String role,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "6") int size,
                                Principal principal) {
        
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
        model.addAttribute("currentPage", "employee");
        return "/employee/list_employee";
    }

    // Add main listing endpoint
    @GetMapping({"", "/"})
    public String mainListEmployees(Model model,
                                    @RequestParam(value = "fullName", required = false) String fullName,
                                    @RequestParam(value = "phone", required = false) String phone,
                                    @RequestParam(value = "role", required = false) String role,
                                    Principal principal) {
        return listEmployees(model, fullName, phone, role, principal);
    }

    @PostMapping("/edit-employee")
    public String editEmployee(@Valid @ModelAttribute("employeeDto") EmployeeEditDto employeeDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               Principal principal) {
        Employee employee = employeeService.findById(employeeDto.getEmployeeId());

        Employee emailCheck = employeeService.findByEmail(employeeDto.getEmail());
        if (emailCheck != null && !emailCheck.getEmployeeId().equals(employee.getEmployeeId())) {
            bindingResult.rejectValue("email", "errorMessage", "Email đã được sử dụng.");
        }

        Employee phoneCheck = employeeService.findByPhone(employeeDto.getPhone());
        if (phoneCheck != null && !phoneCheck.getEmployeeId().equals(employee.getEmployeeId())) {
            bindingResult.rejectValue("phone", "errorMessage", "Số điện thoại đã được sử dụng.");
        }

        if (bindingResult.hasErrors()) {
            List<Employee.Role> allRoles = Arrays.asList(Employee.Role.values());

            List<Employee.Role> filteredRoles = allRoles.stream()
                    .filter(role -> role != Employee.Role.Admin)
                    .toList();
            model.addAttribute("roles", filteredRoles);
            model.addAttribute("currentPage", "employee");
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin nhập vào!");
            return "employee/edit-employee-form";
        }


        BeanUtils.copyProperties(employeeDto, employee);
        employee.setRole(Employee.Role.valueOf(employeeDto.getRole()));
        employeeService.save(employee);
        redirectAttributes.addFlashAttribute("successMessage", "Thay đổi thông tin nhân viên " + employee.getFullName() + " thành công!");
        return "redirect:/employees";
    }
}

