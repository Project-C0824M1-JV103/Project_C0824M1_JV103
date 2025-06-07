package com.example.project_c0824m1_jv103.service.employee;

import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.repository.IEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class EmployeeService implements IEmployeeService {
   @Autowired
    private IEmployeeRepository employeeRepository;

    @Override
    public void deleteEmployeesByIds(List<Integer> employeeIds) {
        employeeRepository.deleteAllById(employeeIds);
    }

    @Override
    public void save(Employee employee) {
        employeeRepository.save(employee);
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findById(Integer id) {
        return employeeRepository.findById(id).get();
    }

    @Override
    public List<Employee> searchEmployees(String fullName, String phone, String role) {
        Employee.Role roleEnum = (role == null || role.isEmpty()) ? null : Employee.Role.valueOf(role);
        return employeeRepository.searchEmployees(fullName, phone, roleEnum);
    }

    @Override
    public Employee findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }
}