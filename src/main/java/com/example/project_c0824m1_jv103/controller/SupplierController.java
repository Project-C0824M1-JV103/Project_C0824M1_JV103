package com.example.project_c0824m1_jv103.controller;


import com.example.project_c0824m1_jv103.model.Supplier;
import com.example.project_c0824m1_jv103.service.supplier.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller

public class SupplierController {

    @Autowired
    private SupplierService supplierService;
    // Hiển thị danh sách nhà cung cấp
    @GetMapping("/supplier")
    public String listSuppliers(Model model) {
        model.addAttribute("suppliers", supplierService.findAll());
        return "supplier/list";
    }
    // Hiển thị form chỉnh sửa nhà cung cấp
    @GetMapping("/supplier/edit/{id}")
    public String showEditSupplierForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Supplier> supplier = supplierService.findById(id);
        if (supplier.isPresent()) {
            model.addAttribute("supplier", supplier.get());
            return "supplier/edit";
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhà cung cấp!");
            return "redirect:/supplier";
        }
    }

    // Xử lý chỉnh sửa nhà cung cấp
    @PostMapping("/supplier/save")
    public String saveSupplier(@ModelAttribute("supplier") Supplier supplier,
                               @RequestParam(value = "image", required = false) MultipartFile image,
                               RedirectAttributes redirectAttributes) {
        try {
            supplierService.saveSupplier(supplier, image);
            redirectAttributes.addFlashAttribute("message", "Chỉnh sửa nhà cung cấp thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi chỉnh sửa nhà cung cấp: " + e.getMessage());
        }
        return "redirect:/supplier";
    }
}