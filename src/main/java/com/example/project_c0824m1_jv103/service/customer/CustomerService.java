package com.example.project_c0824m1_jv103.service.customer;

import com.example.project_c0824m1_jv103.model.Customer;
import com.example.project_c0824m1_jv103.repository.ICustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService implements ICustomerService {
    @Autowired
    private ICustomerRepository iCustomerRepository;

    @Override
    public List<Customer> findAll() {
        return iCustomerRepository.findAll();
    }
}