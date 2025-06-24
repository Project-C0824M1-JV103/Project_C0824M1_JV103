package com.example.project_c0824m1_jv103.service.customer;

import com.example.project_c0824m1_jv103.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {
    List<Customer> findAll();
    List<Customer> searchByNameAndPhone(String keyword);
    Optional<Customer> findById(Integer id);
    Customer save(Customer customer);
    void deleteById(Integer id);
    
    // Pagination methods
    Page<Customer> findAll(Pageable pageable);
    Page<Customer> searchByNameAndPhone(String keyword, Pageable pageable);
    
    // Separate search for name and phone
    Page<Customer> searchByNameAndPhone(String customerName, String phoneNumber, Pageable pageable);

    // Find customer by phone number
    Optional<Customer> findByPhone(String phoneNumber);

    boolean isEmailExists(String email);
    boolean isPhoneExists(String phoneNumber);
}