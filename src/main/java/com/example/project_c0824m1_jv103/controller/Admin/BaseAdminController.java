package com.example.project_c0824m1_jv103.controller.Admin;

import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.service.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

public abstract class BaseAdminController {
    
    @Autowired
    protected AccountService accountService;
    
    @ModelAttribute("currentUser")
    public Employee getCurrentUser(Principal principal) {
        if (principal != null) {
            return accountService.findByEmail(principal.getName());
        }
        return null;
    }
} 