package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.EmployeeCreateDto;
import com.example.project_c0824m1_jv103.dto.EmployeeEditDto;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.service.employee.EmployeeService;
import com.example.project_c0824m1_jv103.service.EmailService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/employees")
public class EmployeeController extends BaseAdminController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @GetMapping("/create")
    public String showCreateForm(Model model, Principal principal) {
        List<Employee.Role> allRoles = Arrays.asList(Employee.Role.values());

        List<Employee.Role> filteredRoles = allRoles.stream()
                .filter(role -> role != Employee.Role.Admin)
                .toList();

        model.addAttribute("employee", new EmployeeCreateDto());
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
        // Kiểm tra trùng email
        if (employeeService.findByEmail(employeeDto.getEmail()) != null) {
            bindingResult.rejectValue("email", "errorMessage", "Email này đã được sử dụng! Vui lòng nhập email khác!");
            List<Employee.Role> filteredRoles = Arrays.stream(Employee.Role.values())
                    .filter(role -> role != Employee.Role.Admin)
                    .toList();
            model.addAttribute("roles", filteredRoles);
            model.addAttribute("currentPage", "employee");
            return "employee/add-employee-form";
        }

        if (bindingResult.hasErrors()) {
            List<Employee.Role> filteredRoles = Arrays.stream(Employee.Role.values())
                    .filter(role -> role != Employee.Role.Admin)
                    .toList();
            model.addAttribute("roles", filteredRoles);
            model.addAttribute("currentPage", "employee");
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin nhập vào!");
            return "employee/add-employee-form";
        }

        Employee employee = new Employee();
        org.springframework.beans.BeanUtils.copyProperties(employeeDto, employee);
        // Set default password if not provided
        String password = (employeeDto.getPassword() == null || employeeDto.getPassword().trim().isEmpty()) 
                ? "123456" : employeeDto.getPassword();
        employee.setPassword(passwordEncoder.encode(password));
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
                .filter(r -> r != Employee.Role.Admin)
                .map(Enum::name)
                .toList();
        model.addAttribute("roles", roles);

        // Tạo pageable
        Pageable pageable = PageRequest.of(page, size);

        Page<Employee> employeePage;
        boolean isSearch = (fullName != null && !fullName.isEmpty()) ||
                (phone != null && !phone.isEmpty()) ||
                (role != null && !role.isEmpty());

        if (!isSearch) {
            employeePage = employeeService.findAllWithPaging(pageable);
        } else {
            employeePage = employeeService.searchEmployeesWithPaging(fullName, phone, role, pageable);
        }

        model.addAttribute("employeePage", employeePage);
        model.addAttribute("listEmployee", employeePage.getContent());
        model.addAttribute("currentPage", "employee");

        // Thêm các thuộc tính phân trang
        model.addAttribute("pageNumber", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("totalElements", employeePage.getTotalElements());
        model.addAttribute("isSearch", isSearch);

        // Thêm tham số tìm kiếm cho liên kết phân trang
        model.addAttribute("fullName", fullName);
        model.addAttribute("phone", phone);
        model.addAttribute("roleParam", role);

        return "/employee/list_employee";
    }

    // Add main listing endpoint
//    @GetMapping({"", "/"})
//    public String mainListEmployees(Model model,
//                                    @RequestParam(value = "fullName", required = false) String fullName,
//                                    @RequestParam(value = "phone", required = false) String phone,
//                                    @RequestParam(value = "role", required = false) String role,
//                                    @RequestParam(value = "page", defaultValue = "0") int page,
//                                    @RequestParam(value = "size", defaultValue = "6") int size,
//                                    Principal principal) {
//        return listEmployees(model, fullName, phone, role, page, size, principal);
//    }

    @PostMapping("/edit-employee")
    public String editEmployee(@Valid @ModelAttribute("employeeDto") EmployeeEditDto employeeDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               Principal principal) {
        Employee employee = employeeService.findById(employeeDto.getEmployeeId());

//        Employee emailCheck = employeeService.findByEmail(employeeDto.getEmail());
//        if (emailCheck != null && !emailCheck.getEmployeeId().equals(employee.getEmployeeId())) {
//            bindingResult.rejectValue("email", "errorMessage", "Email này đã được sử dụng! Vui lòng nhập email khác!");
//        }

        if(employeeDto.getEmail() != null || !employeeDto.getEmail().isEmpty()) {
            if(employeeService.isEmailExists(employeeDto.getEmail(),employeeDto.getEmployeeId())) {
                if (!bindingResult.hasFieldErrors("email")) {
                    bindingResult.rejectValue("email", "errorMessage", "Email này đã được sử dụng! Vui lòng nhập email khác!");
                }
            }
        }

//        Employee phoneCheck = employeeService.findByPhone(employeeDto.getPhone());
//        if (phoneCheck != null && !phoneCheck.getEmployeeId().equals(employee.getEmployeeId())) {
//            bindingResult.rejectValue("phone", "errorMessage", "Số điện thoại này đã được sử dụng! Vui lòng nhập số điện thoại khác!");
//        }

        if(employeeDto.getPhone() != null || !employeeDto.getPhone().isEmpty()) {
            if(employeeService.isPhoneExists(employeeDto.getPhone(),employeeDto.getEmployeeId())) {
                if (!bindingResult.hasFieldErrors("phone")) {
                    bindingResult.rejectValue("phone", "errorMessage", "Số điện thoại này đã được sử dụng! Vui lòng nhập số điện thoại khác!");
                }
            }
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

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("employeeId") Integer employeeId,
                               RedirectAttributes redirectAttributes,
                               Principal principal) {
        try {
            Employee employee = employeeService.findById(employeeId);
            if (employee == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhân viên!");
                return "redirect:/employees";
            }

            // Tạo mật khẩu mặc định
            String newPassword = "123456";
            String encodedPassword = passwordEncoder.encode(newPassword);
            
            // Cập nhật mật khẩu
            employee.setPassword(encodedPassword);
            employeeService.save(employee);
            redirectAttributes.addFlashAttribute("successMessage", "Reset mật khẩu nhân viên " + employee.getFullName() + " thành công!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi reset mật khẩu: " + e.getMessage());
        }
        return "redirect:/employees";
    }

    // API endpoint để kiểm tra email đã tồn tại trong toàn hệ thống
    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@RequestParam("email") String email) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = employeeService.isEmailExistsInSystem(email);
        response.put("exists", exists);
        return response;
    }

    // API endpoint để kiểm tra số điện thoại đã tồn tại trong toàn hệ thống
    @GetMapping("/check-phone")
    @ResponseBody
    public Map<String, Boolean> checkPhone(@RequestParam("phone") String phone) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = employeeService.isPhoneExistsInSystem(phone);
        response.put("exists", exists);
        return response;
    }

    // API endpoint để gửi OTP xác thực email
    @PostMapping("/send-otp")
    @ResponseBody
    public Map<String, Object> sendOtp(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra email format trước khi gửi OTP
            if (email == null || email.trim().isEmpty() || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                response.put("success", false);
                response.put("message", "Email không hợp lệ");
                return response;
            }
            
            // Gửi OTP (sử dụng EmailService)
            boolean sent = emailService.sendOtp(email.trim());
            response.put("success", sent);
            response.put("message", sent ? "OTP đã được gửi đến email của bạn" : "Không thể gửi OTP, vui lòng thử lại");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi gửi OTP");
        }
        return response;
    }

    // API endpoint để xác thực OTP
    @PostMapping("/verify-otp")
    @ResponseBody
    public Map<String, Object> verifyOtp(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = body.get("email");
            String otp = body.get("otp");
            
            if (email == null || otp == null || email.trim().isEmpty() || otp.trim().isEmpty()) {
                response.put("verified", false);
                response.put("message", "Email và OTP không được để trống");
                return response;
            }
            
            // Xác thực OTP (sử dụng EmailService)
            boolean verified = emailService.verifyOtp(email.trim(), otp.trim());
            response.put("verified", verified);
            response.put("message", verified ? "Email đã được xác thực thành công" : "Mã OTP không đúng hoặc đã hết hạn");
        } catch (Exception e) {
            response.put("verified", false);
            response.put("message", "Có lỗi xảy ra khi xác thực OTP");
        }
        return response;
    }

}

