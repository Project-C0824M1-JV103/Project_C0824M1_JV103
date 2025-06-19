package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.model.Supplier;
import com.example.project_c0824m1_jv103.dto.SupplierDto;
import com.example.project_c0824m1_jv103.service.supplier.ISupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/Supplier")
public class SupplierController extends BaseAdminController {

    @Autowired
    private ISupplierService supplierService;

    @GetMapping("")
    public ModelAndView showSupplierList(
            @RequestParam(name = "pageSupplier", required = false, defaultValue = "0") int page,
            @RequestParam(name = "suplierName", required = false) String suplierName,
            @RequestParam(name = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(name = "email", required = false) String email) {
        Pageable pageable = PageRequest.of(page, 6);
        ModelAndView modelAndView = new ModelAndView("supplier/list-supplier");
        modelAndView.addObject("suppliers", supplierService.findByCriteria(suplierName, phoneNumber, email, pageable));
        modelAndView.addObject("suplierName", suplierName);
        modelAndView.addObject("phoneNumber", phoneNumber);
        modelAndView.addObject("email", email);
        modelAndView.addObject("currentPage", "supplier");
        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    public String showEditSupplierForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Supplier> supplier = supplierService.findById(id);
        if (supplier.isPresent()) {
            model.addAttribute("supplier", supplier.get());
            return "supplier/edit";
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhà cung cấp!");
            return "redirect:/Supplier"; // Fixed redirect to match case
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
        return "redirect:/Supplier";
    }

    @GetMapping("/add")
    public String showAddSupplierForm(Model model) {
        model.addAttribute("supplierDto", new SupplierDto());
        return "supplier/add-supplier-form";
    }

    @PostMapping("/add")
    public String addSupplier(@Valid @ModelAttribute("supplierDto") SupplierDto supplierDto,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        System.out.println("Supplier name: " + supplierDto.getSuplierName());
        System.out.println("Has image file: " + (supplierDto.getImageFile() != null && !supplierDto.getImageFile().isEmpty()));

        try {
            // Kiểm tra validation errors cơ bản
            if (bindingResult.hasErrors()) {
                System.out.println("Binding result has errors");
                bindingResult.getAllErrors().forEach(error ->
                    System.out.println("Error: " + error.getDefaultMessage())
                );
                model.addAttribute("error", "Vui lòng kiểm tra lại thông tin đã nhập");
                return "supplier/add-supplier-form";
            }

            // Kiểm tra validation trùng lặp (email và phone)
            String validationError = supplierService.validateNewSupplier(supplierDto);
            if (validationError != null) {
                System.out.println("Custom validation failed: " + validationError);
                model.addAttribute("error", validationError);
                return "supplier/add-supplier-form";
            }

            // Lưu supplier
            supplierService.saveSupplier(supplierDto);

            System.out.println("Supplier added successfully");

            redirectAttributes.addFlashAttribute("message", "Thêm nhà cung cấp thành công!");
            return "redirect:/Supplier";

        } catch (IOException e) {
            System.err.println("Image upload error - " + e.getMessage());
            model.addAttribute("error", "Lỗi khi tải ảnh lên: " + e.getMessage());
            return "supplier/add-supplier-form";
        } catch (RuntimeException e) {
            // Xử lý validation errors từ service
            System.err.println("Runtime error - " + e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "supplier/add-supplier-form";
        } catch (Exception e) {
            System.err.println("Unexpected error - " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "supplier/add-supplier-form";
        }
    }

    // API endpoint để kiểm tra email đã tồn tại
    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@RequestParam("email") String email) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = supplierService.isEmailExists(email);
        response.put("exists", exists);
        return response;
    }

    // API endpoint để kiểm tra số điện thoại đã tồn tại
    @GetMapping("/check-phone")
    @ResponseBody
    public Map<String, Boolean> checkPhoneNumber(@RequestParam("phoneNumber") String phoneNumber) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = supplierService.isPhoneNumberExists(phoneNumber);
        response.put("exists", exists);
        return response;
    }

    // API endpoint để kiểm tra tên nhà cung cấp đã tồn tại
    @GetMapping("/check-name")
    @ResponseBody
    public Map<String, Boolean> checkSupplierName(@RequestParam("suplierName") String suplierName) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = supplierService.isSupplierNameExists(suplierName);
        response.put("exists", exists);
        return response;
    }
}