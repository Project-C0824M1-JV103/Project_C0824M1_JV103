package com.example.project_c0824m1_jv103.service.security;

import com.example.project_c0824m1_jv103.model.Employee;

import java.util.List;

public interface AccountService {
    Employee findByEmail(String email);
    Employee findByRole(Employee.Role role);
    List<Employee> findAll();
}
