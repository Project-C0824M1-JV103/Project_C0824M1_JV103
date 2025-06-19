package com.example.project_c0824m1_jv103.controller;



import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.model.Supplier;

import com.example.project_c0824m1_jv103.service.supplier.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/supplier")
public class SupplierController extends BaseAdminController {

    @Autowired
    private ISupplierService supplierService;


    @GetMapping("")
    public ModelAndView showSupplierList(
            @RequestParam(name = "pageSupplier", required = false, defaultValue = "0") int page,
            @RequestParam(name = "suplierName", required = false) String suplierName,
            @RequestParam(name = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(name = "email", required = false) String email) {
        if (page < 0) {
            page = 0;
        }

        Pageable pageable = PageRequest.of(page, 3);
        Page<Supplier> supplierPage = supplierService.findByCriteria(suplierName, phoneNumber, email, pageable);

        if (page >= supplierPage.getTotalPages() && supplierPage.getTotalPages() > 0) {
            page = 0;
            pageable = PageRequest.of(page, 3);
            supplierPage = supplierService.findByCriteria(suplierName, phoneNumber, email, pageable);
        }
        ModelAndView modelAndView = new ModelAndView("supplier/list-supplier");
        modelAndView.addObject("suppliers", supplierPage);
        modelAndView.addObject("suplierName", suplierName);
        modelAndView.addObject("phoneNumber", phoneNumber);
        modelAndView.addObject("email", email);
        return modelAndView;
    }

    @GetMapping("/edit/{id}") // Removed redundant "/Supplier" since @RequestMapping handles it
    public String showEditSupplierForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Supplier> supplier = supplierService.findById(id);
        if (supplier.isPresent()) {
            model.addAttribute("supplier", supplier.get());
            return "supplier/edit";
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhà cung cấp!");
            return "redirect:/supplier"; // Fixed redirect to match case
        }
    }

    @PostMapping("/save")
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