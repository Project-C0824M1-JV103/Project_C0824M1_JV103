package com.example.project_c0824m1_jv103.service.customer;

import com.example.project_c0824m1_jv103.model.Customer;

import java.util.List;

public interface ICustomerService {
    List<Customer> findAll();
    List<Customer> searchByNameAndPhone(String keyword);
}