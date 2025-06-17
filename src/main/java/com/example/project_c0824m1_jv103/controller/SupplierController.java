package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.SupplierDto;
import com.example.project_c0824m1_jv103.service.supplier.ISupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/supplier")
public class SupplierController extends BaseAdminController {
    @Autowired
    private ISupplierService supplierService;

    @GetMapping("")
    public ModelAndView showSupplierList() {
        return new ModelAndView("supplier/list-supplier").addObject("suppliers", supplierService.findAll());
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
            return "redirect:/supplier";
            
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
}