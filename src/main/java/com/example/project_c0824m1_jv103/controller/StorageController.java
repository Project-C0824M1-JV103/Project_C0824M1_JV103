package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.StorageExportDTO;
import com.example.project_c0824m1_jv103.dto.ProductDTO;
import com.example.project_c0824m1_jv103.model.Product;
import com.example.project_c0824m1_jv103.model.Storage;
import com.example.project_c0824m1_jv103.repository.IProductRepository;
import com.example.project_c0824m1_jv103.service.product.IProductService;
import com.example.project_c0824m1_jv103.service.storage.IStorageService;
import com.example.project_c0824m1_jv103.service.supplier.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/storage")
public class StorageController extends BaseAdminController {

    @Autowired
    private IStorageService storageService;

    @Autowired
    private ISupplierService supplierService;
    private IProductService productService;

    @Autowired
    private IProductRepository productRepository;

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