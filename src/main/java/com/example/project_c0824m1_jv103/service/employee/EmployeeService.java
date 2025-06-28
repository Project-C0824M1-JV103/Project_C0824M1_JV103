package com.example.project_c0824m1_jv103.service.employee;

import com.example.project_c0824m1_jv103.dto.EmployeePersonalDto;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.repository.IEmployeeRepository;
import com.example.project_c0824m1_jv103.service.customer.ICustomerService;
import com.example.project_c0824m1_jv103.service.supplier.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class EmployeeService implements IEmployeeService {
   @Autowired
    private IEmployeeRepository employeeRepository;
    
    @Autowired
    @Lazy
    private ICustomerService customerService;
    
    @Autowired
    private ISupplierService supplierService;

    @Override
    public void deleteEmployeesByIds(List<Integer> employeeIds) {
        List<Employee> employees = employeeRepository.findAllById(employeeIds);
        for (Employee employee : employees) {
            employee.setStatus(Employee.Status.inactive);
        }
        employeeRepository.saveAll(employees);
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
    @Override
    public Page<Employee> findAllWithPaging(Pageable pageable) {
        return employeeRepository.findAllNonAdminWithPaging(pageable);
    }
    @Override
    public Page<Employee> findAllNonAdminWithPaging(Pageable pageable) {
        return employeeRepository.findAllNonAdminWithPaging(pageable);
    }
//    @Override
//    public Page<Employee> findAllWithPaging(Pageable pageable) {
//        return employeeRepository.findAll(pageable);
//    }

    @Override
    public Page<Employee> searchEmployeesWithPaging(String fullName, String phone, String role, Pageable pageable) {
        Employee.Role roleEnum = (role == null || role.isEmpty()) ? null : Employee.Role.valueOf(role);
        return employeeRepository.searchEmployeesWithPaging(fullName, phone, roleEnum, pageable);
    }

    @Override
    public Employee updateEmployeeInfo(EmployeePersonalDto employeePersonalDto) {
        Employee existingEmployee = employeeRepository.findById(employeePersonalDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee không tồn tại."));


        existingEmployee.setFullName(employeePersonalDto.getFullName());
        existingEmployee.setEmail(employeePersonalDto.getEmail());
        existingEmployee.setPhone(employeePersonalDto.getPhone());

        return employeeRepository.save(existingEmployee);
    }

    @Override
    public Employee findByPhone(String phone) {
        return employeeRepository.findByPhone(phone);
    }
    
    @Override
    public boolean isEmailExistsForOtherEmployee(String email, Integer employeeId) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Kiểm tra trùng với Employee khác
        boolean existsInEmployee = employeeRepository.existsByEmailIgnoreCaseAndEmployeeIdNot(email, employeeId);
        
        // Kiểm tra trùng với Customer
        boolean existsInCustomer = customerService.isEmailExists(email);
        
        // Kiểm tra trùng với Supplier
        boolean existsInSupplier = supplierService.isEmailExists(email);
        
        return existsInEmployee || existsInCustomer || existsInSupplier;
    }

    @Override
    public boolean isPhoneExistsForOtherEmployee(String phone, Integer employeeId) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        // Kiểm tra trùng với Employee khác
        boolean existsInEmployee = employeeRepository.existsByPhoneAndEmployeeIdNot(phone, employeeId);
        
        // Kiểm tra trùng với Customer
        boolean existsInCustomer = (customerService.findByPhone(phone).isPresent());
        
        // Kiểm tra trùng với Supplier
        boolean existsInSupplier = supplierService.isPhoneNumberExists(phone);
        
        return existsInEmployee || existsInCustomer || existsInSupplier;
    }
}