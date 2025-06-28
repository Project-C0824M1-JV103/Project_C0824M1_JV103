package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.*;
import com.example.project_c0824m1_jv103.dto.StorageImportDTO;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.model.Product;
import com.example.project_c0824m1_jv103.model.ProductImages;
import com.example.project_c0824m1_jv103.model.Storage;
import com.example.project_c0824m1_jv103.repository.ICategoryRepository;
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
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

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
    private ICategoryRepository categoryRepository;

    @GetMapping
    public String storage(Model model) {
        model.addAttribute("currentPage", "storage");
        return "storage/storage-page";
    }

    @GetMapping("/list-import")
    public String showStorageList(
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Xử lý ngày
        LocalDate start = null;
        LocalDate end = null;
        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDate.parse(startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDate.parse(endDate);
            }
        } catch (DateTimeParseException e) {
            model.addAttribute("error", "Định dạng ngày không hợp lệ. Vui lòng sử dụng định dạng YYYY-MM-DD.");
        }

        List<StorageDto> filteredStorages = storageService.findByCriteria(productName, start, end);
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<StorageDto> storagePage = storageService.paginateStorageList(filteredStorages, pageable);

        // Đặt tên biến nhất quán (storagePage thay vì storagesPage)
        model.addAttribute("storagePage", storagePage);
        model.addAttribute("productName", productName);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", storagePage.getTotalPages());
        model.addAttribute("totalItems", storagePage.getTotalElements());
        model.addAttribute("suppliers", supplierService.findAll(Pageable.unpaged()));

        return "storage/list";
    }

    @GetMapping("/show-create")
    public String showCreateStorage(Model model) {
        LOGGER.info("Handling /storage/show-create request");
        model.addAttribute("inforStorages", storageService.findAllStorage());
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("categorys", categoryRepository.findAll());
        model.addAttribute("storageImportDTO", new StorageImportDTO());
        model.addAttribute("importStorageProduct", new StorageImportProduct());
        return "storage/import-storage";
    }

    @PostMapping("/create")
    public String importProduct(@Valid @ModelAttribute("storageImportDTO") StorageImportDTO dto,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            Product product = productRepository.findById(dto.getProductId()).orElse(null);
            if (product != null) {
                List<String> imageUrls = product.getProductImages().stream()
                        .map(img -> img.getImageUrl() != null ? img.getImageUrl() : "https://via.placeholder.com/150")
                        .toList();
                dto.setProductImages(imageUrls);
            }

            model.addAttribute("storageImportDTO", dto);
            model.addAttribute("inforStorages", storageService.findAllStorage());
            model.addAttribute("suppliers", supplierService.findAll());
            model.addAttribute("importStorageProduct", new StorageImportProduct());
            return "storage/import-storage";
        }
        try {
            storageService.importProduct(dto);
            return "redirect:/storage";
        } catch (RuntimeException e) {
            Product product = productRepository.findById(dto.getProductId()).orElse(null);
            if (product != null) {
                model.addAttribute("product", product);
                model.addAttribute("productImage", product.getProductImages());
            }

            model.addAttribute("storage", dto);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("inforStorages", storageService.findAllStorage());
            model.addAttribute("suppliers", supplierService.findAll());
            model.addAttribute("importStorageProduct", new StorageImportProduct());
            return "storage/import-storage";
        }
    }

    @PostMapping("/create-product")
    public String createProduct(@Valid @ModelAttribute("importStorageProduct") StorageImportProduct dto, 
                               BindingResult result, 
                               RedirectAttributes redirectAttributes, 
                               Model model) {
        if (result.hasErrors()) {
            // Thêm lại các attribute cần thiết cho form
            model.addAttribute("inforStorages", storageService.findAllStorage());
            model.addAttribute("suppliers", supplierService.findAll());
            model.addAttribute("categorys", categoryRepository.findAll());
            model.addAttribute("storageImportDTO", new StorageImportDTO());
            model.addAttribute("importStorageProduct", dto); // Set lại dto có lỗi
            model.addAttribute("showAddProductModal", true); // Flag để hiển thị modal
            return "storage/import-storage";
        }
        try {
            dto.setPrice(0.0);
            dto.setQuantity(0);
            var product = productService.createProductFromImport(dto);
            Integer employeeId = null;
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
                    String email = authentication.getName();
                    Employee employee = employeeService.findByEmail(email);
                    if (employee != null) {
                        employeeId = employee.getEmployeeId();
                    }
                }
            } catch (Exception ex) {
                employeeId = null;
            }
            storageService.importProduct(new StorageImportId(product.getProductId(), employeeId));
            redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm mới thành công!");
            return "redirect:/storage/show-create";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi thêm sản phẩm mới: " + e.getMessage());
            return "redirect:/storage/show-create";
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
        Page<Storage> storagePage = storageService.searchProductsInStorage(keyword, PageRequest.of(page, size));
        
        // Chuyển đổi Page<Storage> thành Page<Product> để tương thích với view
        Page<Product> productPage = storagePage.map(Storage::getProduct);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
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
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Storage storage = storageService.findByProductId(id)
                .orElse(new Storage()); // Tạo storage rỗng nếu không tìm thấy

        return new StorageExportDTO(
                product.getProductId(),
                product.getProductName(),
                product.getSupplier() != null ? product.getSupplier().getSuplierName() : "N/A",
                storage.getQuantity() != null ? storage.getQuantity() : 0, // Lấy số lượng từ storage, mặc định là 0
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

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        try {
            Storage storage = storageService.getStorageById(id);

            // Convert to StorageDto
            StorageDto storageDto = new StorageDto();
            storageDto.setStorageId(storage.getStorageId());
            storageDto.setProductId(storage.getProduct().getProductId());
            storageDto.setQuantity(storage.getQuantity());
            storageDto.setCost(storage.getCost());
            storageDto.setEmployeeId(storage.getEmployee() != null ? storage.getEmployee().getEmployeeId() : null);
            storageDto.setTransactionDate(storage.getTransactionDate());

            model.addAttribute("storageDto", storageDto);
            model.addAttribute("storage", storage);
            model.addAttribute("suppliers", supplierService.findAll());
            model.addAttribute("employees", employeeService.findAll());

            return "storage/edit-import-storage";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/storage";
        }
    }

    @PostMapping("/update/{id}")
    public String updateStorage(@PathVariable Integer id,
                               @Valid @ModelAttribute StorageDto storageDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            try {
                Storage storage = storageService.getStorageById(id);
                model.addAttribute("storage", storage);
                model.addAttribute("suppliers", supplierService.findAll());
                model.addAttribute("employees", employeeService.findAll());
                model.addAttribute("error", "Vui lòng kiểm tra lại dữ liệu nhập vào");
                return "storage/edit-import-storage";
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/storage";
            }
        }

        try {
            storageService.updateStorage(id, storageDto);
            redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin nhập kho thành công");
            return "redirect:/storage";
        } catch (RuntimeException e) {
            try {
                Storage storage = storageService.getStorageById(id);
                model.addAttribute("storage", storage);
                model.addAttribute("suppliers", supplierService.findAll());
                model.addAttribute("employees", employeeService.findAll());
                model.addAttribute("error", e.getMessage());
                return "storage/edit-import-storage";
            } catch (RuntimeException ex) {
                redirectAttributes.addFlashAttribute("error", ex.getMessage());
                return "redirect:/storage";
            }
        }
    }

    // API để lấy sản phẩm theo nhà cung cấp
    @GetMapping("/api/products-by-supplier/{supplierId}")
    @ResponseBody
    public List<Product> getProductsBySupplier(@PathVariable Integer supplierId) {
        return productRepository.findBySupplier_SuplierId(supplierId);
    }

    @GetMapping("/products/data")
    @ResponseBody
    public Map<String, Object> getProductsData(
            @RequestParam(required = false) String productName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Storage> storages;
        boolean isSearch = false;

        if (productName != null && !productName.isEmpty()) {
            storages = storageService.searchProductsInStorage(productName, pageable);
            isSearch = true;
        } else {
            storages = storageService.getAllStorageRecords(pageable);
        }

        // Chuyển đổi Storage thành ProductDTO để tương thích với frontend
        List<Map<String, Object>> products = storages.getContent().stream()
            .map(storage -> {
                Map<String, Object> product = new HashMap<>();
                product.put("productId", storage.getProduct().getProductId());
                product.put("productName", storage.getProduct().getProductName());
                product.put("price", storage.getCost() != null ? storage.getCost() : storage.getProduct().getPrice());
                product.put("cpu", storage.getProduct().getCpu());
                product.put("memory", storage.getProduct().getMemory());
                product.put("supplierId", storage.getProduct().getSupplier() != null ? storage.getProduct().getSupplier().getSuplierId() : null);
                product.put("supplierName", storage.getProduct().getSupplier() != null ? storage.getProduct().getSupplier().getSuplierName() : null);
                product.put("quantity", storage.getQuantity());
                return product;
            })
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("pageNumber", page);
        response.put("pageSize", size);
        response.put("totalPages", storages.getTotalPages());
        response.put("totalElements", storages.getTotalElements());
        response.put("isSearch", isSearch);

        return response;
    }
}