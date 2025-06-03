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
    public List<Employee> findAllEmployee() {
        return employeeRepository.findAll();
    }
    
    @Override
    public void deleteEmployeesByIds(List<Integer> employeeIds) {
        employeeRepository.deleteAllById(employeeIds);
    }
}