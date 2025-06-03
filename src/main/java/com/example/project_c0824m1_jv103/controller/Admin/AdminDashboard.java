package com.example.project_c0824m1_jv103.controller.Admin;


import com.example.project_c0824m1_jv103.model.Customer;
import com.example.project_c0824m1_jv103.service.customer.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/Admin")
public class AdminDashboard {
    @Autowired
    private ICustomerService iCustomerService;

    @GetMapping("/Customer")
    public String showListCutomer(Model model){
        List<Customer> customers = iCustomerService.findAll();
        model.addAttribute("customers",customers);
        model.addAttribute("currentPage", "customer");
        return "admin/showListCustomer";
    }
}
