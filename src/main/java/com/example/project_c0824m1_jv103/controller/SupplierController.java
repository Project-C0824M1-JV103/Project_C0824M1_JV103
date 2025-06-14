package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.service.supplier.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/Supplier")
public class SupplierController {
    @Autowired
    private ISupplierService supplierService;

    @GetMapping("")
    public ModelAndView showSupplierList(@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 3);
        return new ModelAndView("supplier/list-supplier").addObject("suppliers", supplierService.findAll(pageable));
    }
}
