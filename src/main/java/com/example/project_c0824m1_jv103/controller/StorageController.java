package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.StorageExportDTO;
import com.example.project_c0824m1_jv103.dto.ProductDTO;
import com.example.project_c0824m1_jv103.dto.StorageImportDTO;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.model.Product;
import com.example.project_c0824m1_jv103.model.Storage;
import com.example.project_c0824m1_jv103.repository.IProductRepository;
import com.example.project_c0824m1_jv103.service.employee.IEmployeeService;
import com.example.project_c0824m1_jv103.service.product.IProductService;
import com.example.project_c0824m1_jv103.service.storage.IStorageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/storage")
public class StorageController extends BaseAdminController {

    @Autowired
    private IStorageService storageService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IProductRepository productRepository;
    @Autowired
    private IEmployeeService employeeService;

    @GetMapping("/import")
    public String showImportForm(Model model) {
        model.addAttribute("storageImportDTO", new StorageImportDTO());
        List<ProductDTO> products = productService.getAllProducts();
        List<Employee> employees = employeeService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("employees", employees);
        return "storage/import";
    }
    @PostMapping("/import")
    public String importProduct(@Valid @ModelAttribute("storageImportDTO") StorageImportDTO importDTO,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            List<ProductDTO> products = productService.getAllProducts();
            List<Employee> employees = employeeService.findAll();
            model.addAttribute("products", products);
            model.addAttribute("employees", employees);
            return "storage/import";
        }
        try {
            storageService.importProduct(importDTO);
            redirectAttributes.addFlashAttribute("message", "Nhập kho thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi nhập kho: " + e.getMessage());
            return "redirect:/storage/import";
        }
        return "redirect:/storage/import-history";
    }@GetMapping("/import-history")
    public String showImportHistory(Model model,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "size", defaultValue = "6") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Storage> importHistory = storageService.getImportHistory(pageable);
        model.addAttribute("importHistory", importHistory);
        model.addAttribute("pageNumber", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", importHistory.getTotalPages());
        model.addAttribute("totalElements", importHistory.getTotalElements());
        return "storage/import-history";
    }
    @GetMapping("/export")
    public String showExportForm(Model model) {
        model.addAttribute("exportDTO", new StorageExportDTO());
        model.addAttribute("currentPage", "export");
        return "storage/export-form";
    }

    @GetMapping("/products")
    public String showProductSelection(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<ProductDTO> products = productService.searchProducts(keyword, "productName", PageRequest.of(page, size));
        model.addAttribute("products", products.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("currentPage", "export");
        return "storage/product-selection";
    }

    @PostMapping("/export")
    public String exportProduct(@ModelAttribute StorageExportDTO exportDTO,
                              RedirectAttributes redirectAttributes) {
        try {
            storageService.exportProduct(exportDTO);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Xuất kho " + exportDTO.getProductName() + " thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/storage/export";
        }
        return "redirect:/storage/export";
    }

    @GetMapping("/product/{id}")
    @ResponseBody
    public StorageExportDTO getProductInfo(@PathVariable Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) throw new RuntimeException("Product not found");
        return new StorageExportDTO(
            product.getProductId(),
            product.getProductName(),
            product.getSupplier().getSuplierName(),
            product.getQuantity(),
            null
        );
    }

    @GetMapping("/history")
    public String exportHistory(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        // Lấy tất cả bản ghi xuất kho (quantity < 0)
        Page<Storage> exportRecords = storageService.getExportHistory(PageRequest.of(page, size));
        model.addAttribute("exportRecords", exportRecords.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", exportRecords.getTotalPages());
        model.addAttribute("totalElements", exportRecords.getTotalElements());
        model.addAttribute("currentPage", "export");
        return "storage/export-history";
    }
} 