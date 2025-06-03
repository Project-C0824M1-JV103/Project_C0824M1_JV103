package com.example.project_c0824m1_jv103.service.employee;

import com.example.project_c0824m1_jv103.model.Employee;

import java.util.List;

public interface IEmployeeService {
    List<Employee> findAllEmployee();
    void deleteEmployeesByIds(List<Integer> employeeIds);
}