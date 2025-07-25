package com.example.project_c0824m1_jv103.service.employee;

import com.example.project_c0824m1_jv103.dto.EmployeePersonalDto;
import com.example.project_c0824m1_jv103.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IEmployeeService {
    void deleteEmployeesByIds(List<Integer> employeeIds);
    void save(Employee employee);
    List<Employee> findAll();
    Employee findById(Integer id);
    List<Employee> searchEmployees(String fullName, String phone, String role);
    Employee findByEmail(String email);

    // Pagination methods
    Page<Employee> findAllWithPaging(Pageable pageable);
    Page<Employee> searchEmployeesWithPaging(String fullName, String phone, String role, Pageable pageable);
    Employee updateEmployeeInfo(EmployeePersonalDto employeePersonalDto);
    Employee findByPhone(String phone);

    Page<Employee> findAllNonAdminWithPaging(Pageable pageable); // Thêm phương thức mới
    
    // Validation methods for edit employee (excluding current employee)
    boolean isEmailExistsForOtherEmployee(String email, Integer employeeId);
    boolean isPhoneExistsForOtherEmployee(String phone, Integer employeeId);

    boolean isEmailExists(String email, Integer employeeId);
    boolean isPhoneExists(String phoneNumber, Integer employeeId);

    // System-wide validation methods
    boolean isEmailExistsInSystem(String email);
    boolean isPhoneExistsInSystem(String phone);
}