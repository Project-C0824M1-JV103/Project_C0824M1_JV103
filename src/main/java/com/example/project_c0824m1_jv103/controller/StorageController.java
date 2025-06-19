package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.service.storage.IStorageService;
import com.example.project_c0824m1_jv103.service.supplier.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/storage")
public class StorageController extends BaseAdminController {
    @Autowired
    private IStorageService storageService;

    @Autowired
    private ISupplierService supplierService;

    @GetMapping("")
    public ModelAndView show(){
        return new ModelAndView("storage/list-storage").addObject("storages", storageService.findAll());
    }

    @GetMapping("show-create")
    public ModelAndView showCreateStorage() {
        ModelAndView modelAndView = new ModelAndView("storage/add-storage");
        modelAndView.addObject("inforStorages", storageService.findAll());
        modelAndView.addObject("suppliers", supplierService.findAll());
        return modelAndView;
    }
}
