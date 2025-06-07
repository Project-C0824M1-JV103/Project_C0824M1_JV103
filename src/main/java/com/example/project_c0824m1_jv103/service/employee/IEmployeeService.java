package com.example.project_c0824m1_jv103.service.employee;

import com.example.project_c0824m1_jv103.dto.EmployeePersonalDto;
import com.example.project_c0824m1_jv103.model.Employee;
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
    Employee updateEmployeeInfo(EmployeePersonalDto employeePersonalDto);
}