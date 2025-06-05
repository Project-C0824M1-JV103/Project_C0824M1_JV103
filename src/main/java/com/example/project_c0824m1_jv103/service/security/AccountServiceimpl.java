package com.example.project_c0824m1_jv103.service.security;


import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceimpl implements AccountService {
    @Autowired
    AccountRepository accountRepository;

    @Override
    public Employee findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public Employee findByRole(Employee.Role role) {
        return accountRepository.findByRole(role);
    }

    @Override
    public List<Employee> findAll() {
        return accountRepository.findAll();
    }
}
