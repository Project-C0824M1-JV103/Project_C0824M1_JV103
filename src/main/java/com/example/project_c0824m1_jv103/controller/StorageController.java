package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.StorageDto;
import com.example.project_c0824m1_jv103.dto.StorageExportDTO;
import com.example.project_c0824m1_jv103.dto.ProductDTO;
import com.example.project_c0824m1_jv103.dto.StorageImportDTO;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.dto.StorageImportDTO;
import com.example.project_c0824m1_jv103.model.Product;
import com.example.project_c0824m1_jv103.model.Storage;
import com.example.project_c0824m1_jv103.repository.IProductRepository;
import com.example.project_c0824m1_jv103.service.employee.IEmployeeService;
import com.example.project_c0824m1_jv103.service.product.IProductService;
import com.example.project_c0824m1_jv103.service.storage.IStorageService;
import com.example.project_c0824m1_jv103.service.supplier.ISupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
@RequestMapping("/storage")
public class StorageController extends BaseAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageController.class);

    @Autowired
    private IStorageService storageService;

    @Autowired
    private ISupplierService supplierService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IProductRepository productRepository;
    @Autowired
    private IEmployeeService employeeService;
    @Autowired
    private ISupplierService supplierService;
    @GetMapping("/list")
    public String showStorageList(
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Model model) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
        List<StorageDto> storages = storageService.findByCriteria(productName, start, end);
        if (storages == null) {
            model.addAttribute("storages", List.of());
        } else {
            model.addAttribute("storages", storages);
        }
        model.addAttribute("productName", productName);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "storage/list";
    }

    @GetMapping("/show-create")
    public ModelAndView showCreateStorage() {
        LOGGER.info("Handling /storage/show-create request");
        ModelAndView modelAndView = new ModelAndView("storage/import-storage");
        modelAndView.addObject("inforStorages", storageService.findAll()); // hoặc storageService.findAll()
        modelAndView.addObject("suppliers", supplierService.findAll());
        modelAndView.addObject("storageImportDTO", new StorageImportDTO());
        return modelAndView;
    }

    @PostMapping("/create")
    public String importProduct(
            @Valid @ModelAttribute("storageImportDTO") StorageImportDTO importDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("availableProducts", storageService.findAll());
            model.addAttribute("suppliers", supplierService.findAll());
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại dữ liệu nhập vào");
            return "storage/import-storage";
        }

        try {
            storageService.importProduct(importDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Nhập kho sản phẩm \"" + importDTO.getProductName() + "\" thành công.");
            return "redirect:/storage";
        } catch (RuntimeException e) {
            model.addAttribute("availableProducts", storageService.findAll());
            model.addAttribute("suppliers", supplierService.findAll());
            model.addAttribute("errorMessage", e.getMessage());
            return "storage/import-storage";
        }
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
    public String exportProduct(@Valid @ModelAttribute StorageExportDTO exportDTO,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("exportDTO", exportDTO);
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại dữ liệu nhập vào");
            return "storage/export-form";
        }
        
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