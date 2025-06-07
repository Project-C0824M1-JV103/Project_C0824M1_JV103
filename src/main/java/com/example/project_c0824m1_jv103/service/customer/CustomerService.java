package com.example.project_c0824m1_jv103.service.customer;

import com.example.project_c0824m1_jv103.model.Customer;
import com.example.project_c0824m1_jv103.repository.ICustomerRepository;
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
}