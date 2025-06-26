package com.example.project_c0824m1_jv103.service.customer;

import com.example.project_c0824m1_jv103.model.Customer;
import com.example.project_c0824m1_jv103.repository.ICustomerRepository;
import com.example.project_c0824m1_jv103.service.employee.IEmployeeService;
import com.example.project_c0824m1_jv103.service.supplier.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService implements ICustomerService {
    @Autowired
    private ICustomerRepository iCustomerRepository;
    
    @Autowired
    private IEmployeeService employeeService;
    
    @Autowired
    private ISupplierService supplierService;

    @Override
    public List<Customer> findAll() {
        return iCustomerRepository.findAll();
    }

    @Override
    public List<Customer> searchByNameAndPhone(String keyword) {
        return iCustomerRepository.searchByNameOrPhone(keyword);
    }

    @Override
    public Optional<Customer> findById(Integer id) {
        return iCustomerRepository.findById(id);
    }

    @Override
    public Customer save(Customer customer) {
        return iCustomerRepository.save(customer);
    }

    @Override
    public void deleteById(Integer id) {
        iCustomerRepository.deleteById(id);
    }

    // Pagination methods
    @Override
    public Page<Customer> findAll(Pageable pageable) {
        return iCustomerRepository.findAll(pageable);
    }

    @Override
    public Page<Customer> searchByNameAndPhone(String keyword, Pageable pageable) {
        return iCustomerRepository.searchByNameOrPhone(keyword, pageable);
    }

    // Separate search for name and phone
    @Override
    public Page<Customer> searchByNameAndPhone(String customerName, String phoneNumber, Pageable pageable) {
        return iCustomerRepository.searchByNameAndPhone(customerName, phoneNumber, pageable);
    }

    @Override
    public Optional<Customer> findByPhone(String phoneNumber) {
        return iCustomerRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean isEmailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Kiểm tra trùng với Customer
        boolean existsInCustomer = iCustomerRepository.existsByEmailIgnoreCase(email);
        
        // Kiểm tra trùng với Employee
        boolean existsInEmployee = (employeeService.findByEmail(email) != null);
        
        // Kiểm tra trùng với Supplier
        boolean existsInSupplier = supplierService.isEmailExists(email);
        
        return existsInCustomer || existsInEmployee || existsInSupplier;
    }

    @Override
    public boolean isEmailExistsForOtherCustomer(String email, Integer customerId) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Kiểm tra trùng với Customer khác
        boolean existsInCustomer = iCustomerRepository.existsByEmailIgnoreCaseAndCustomerIdNot(email, customerId);
        
        // Kiểm tra trùng với Employee
        boolean existsInEmployee = (employeeService.findByEmail(email) != null);
        
        // Kiểm tra trùng với Supplier  
        boolean existsInSupplier = supplierService.isEmailExists(email);
        
        return existsInCustomer || existsInEmployee || existsInSupplier;
    }

    @Override
    public boolean isPhoneExistsForOtherCustomer(String phoneNumber, Integer customerId) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return iCustomerRepository.existsByPhoneNumberAndCustomerIdNot(phoneNumber, customerId);
    }

    @Override
    public boolean isPhoneExists(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return iCustomerRepository.existsByPhoneNumber(phoneNumber);
    }
}