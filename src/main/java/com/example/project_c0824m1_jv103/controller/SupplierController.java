package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.model.Supplier;
import com.example.project_c0824m1_jv103.dto.SupplierDto;
import com.example.project_c0824m1_jv103.service.supplier.ISupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
        if (page < 0) {
            page = 0;
        }

        Pageable pageable = PageRequest.of(page, 6);
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
        modelAndView.addObject("currentPage", "supplier");
        return modelAndView;
    }

//    @GetMapping("/edit/{id}")
//    public String showEditSupplierForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
//        Optional<Supplier> supplier = supplierService.findById(id);
//        if (supplier.isPresent()) {
//            model.addAttribute("supplier", supplier.get());
//            model.addAttribute("currentPage", "supplier");
//            return "supplier/edit";
//        } else {
//            redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhà cung cấp!");
//            return "redirect:/Supplier"; // Fixed redirect to match case
//        }
//    }
//
//    @PostMapping("/save")
//    public String saveSupplier(@Valid @ModelAttribute("supplier") Supplier supplier,
//                               BindingResult result,
//                               @RequestParam(value = "image", required = false) MultipartFile image,
//                               RedirectAttributes redirectAttributes) {
//        if (result.hasErrors()) {
//            return "supplier/edit"; // Trả về lại form nếu có lỗi
//        }
//        try {
//            supplierService.saveSupplier(supplier, image);
//            redirectAttributes.addFlashAttribute("message", "Chỉnh sửa nhà cung cấp thành công!");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Lỗi khi chỉnh sửa nhà cung cấp: " + e.getMessage());
//        }
//        return "redirect:/Supplier";
//    }
@GetMapping("/edit/{id}")
public String showEditSupplierForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
    Optional<Supplier> supplier = supplierService.findById(id);
    if (supplier.isPresent()) {
        model.addAttribute("supplier", supplier.get());
        model.addAttribute("currentPage", "supplier");
        return "supplier/edit";
    } else {
        redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhà cung cấp!");
        return "redirect:/Supplier";
    }
}

    @PostMapping("/save")
    public String saveSupplier(
            @Valid @ModelAttribute("supplier") Supplier supplier,
            BindingResult result,
            @RequestParam(value = "image", required = false) MultipartFile image,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("supplier", supplier);
            return "supplier/edit";
        }

        try {
            Optional<Supplier> existingSupplier = supplierService.findById(supplier.getSuplierId());
            if (existingSupplier.isPresent()) {
                Supplier currentSupplier = existingSupplier.get();
                SupplierDto supplierDto = new SupplierDto();
                supplierDto.setSuplierId(supplier.getSuplierId());
                supplierDto.setSuplierName(supplier.getSuplierName());
                supplierDto.setEmail(supplier.getEmail());
                supplierDto.setPhoneNumber(supplier.getPhoneNumber());
                supplierDto.setImageFile(image);

                String validationError = supplierService.validateNewSupplier(supplierDto);
                if (validationError != null) {
                    model.addAttribute("supplier", supplier);
                    model.addAttribute("error", validationError);
                    return "supplier/edit";
                }

                supplierService.saveSupplier(supplier, image);
                redirectAttributes.addFlashAttribute("message", "Chỉnh sửa nhà cung cấp thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhà cung cấp để cập nhật!");
            }
        } catch (Exception e) {
            model.addAttribute("supplier", supplier);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi chỉnh sửa nhà cung cấp: " + e.getMessage());
        }
        return "redirect:/Supplier";
    }

    @GetMapping("/add")
    public String showAddSupplierForm(Model model) {
        model.addAttribute("supplierDto", new SupplierDto());
        model.addAttribute("currentPage", "supplier");
        return "supplier/add-supplier-form";
    }

    @PostMapping("/add")
    public String addSupplier(@Valid @ModelAttribute("supplierDto") SupplierDto supplierDto,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        try {
            // Kiểm tra validation errors cơ bản
            if (bindingResult.hasErrors()) {
                model.addAttribute("error", "Vui lòng kiểm tra lại thông tin đã nhập");
                model.addAttribute("currentPage", "supplier");
                return "supplier/add-supplier-form";
            }

            // Kiểm tra validation trùng lặp (email và phone)
            String validationError = supplierService.validateNewSupplier(supplierDto);
            if (validationError != null) {
                model.addAttribute("error", validationError);
                model.addAttribute("currentPage", "supplier");
                return "supplier/add-supplier-form";
            }

            // Lưu supplier
            supplierService.saveSupplier(supplierDto);

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