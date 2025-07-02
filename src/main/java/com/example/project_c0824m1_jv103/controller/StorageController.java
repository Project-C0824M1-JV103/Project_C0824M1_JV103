package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.*;
import com.example.project_c0824m1_jv103.dto.StorageImportDTO;
import com.example.project_c0824m1_jv103.dto.StorageImportProductDTO;
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
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam(defaultValue = "6") int size,
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
        model.addAttribute("currentPage", "storage");
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", storagePage.getTotalPages());
        model.addAttribute("totalItems", storagePage.getTotalElements());
        model.addAttribute("suppliers", supplierService.findAll(Pageable.unpaged()));

        return "storage/list";
    }

    @GetMapping("/show-create")
    public String showCreateStorage(Model model) {
        model.addAttribute("inforStorages", storageService.findAllStorage());
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("categorys", categoryRepository.findAll());
        model.addAttribute("storageImportDTO", new StorageImportDTO());
        model.addAttribute("productDTO", new StorageImportProductDTO());
        model.addAttribute("currentPage", "storage");
        return "storage/import-storage";
    }

    @PostMapping("/create")
    public String importProduct(HttpServletRequest request,
                                @Valid @ModelAttribute("storageImportDTO") StorageImportDTO dto,
                                BindingResult result,
                                @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        String isNewProduct = request.getParameter("isNewProduct");
        if ("true".equals(isNewProduct)) {
            String newProductName = request.getParameter("newProductName");
            String size = request.getParameter("size");
            String cameraBack = request.getParameter("cameraBack");
            String cameraFront = request.getParameter("cameraFront");
            String cpu = request.getParameter("cpu");
            String memory = request.getParameter("memory");
            String categoryIdStr = request.getParameter("categoryId");
            String supplierIdStr = request.getParameter("supplierId");
            String importQuantityStr = request.getParameter("importQuantity");
            String costStr = request.getParameter("cost");

            boolean hasError = false;
            if (newProductName == null || newProductName.trim().isEmpty()) {
                model.addAttribute("errorMessage", "Tên sản phẩm không được để trống");
                hasError = true;
            }
            if (size == null || size.trim().isEmpty()) {
                model.addAttribute("errorMessage", "Kích thước màn hình không được để trống");
                hasError = true;
            }
            if (cameraBack == null || cameraBack.trim().isEmpty()) {
                model.addAttribute("errorMessage", "Camera sau không được để trống");
                hasError = true;
            }
            if (cameraFront == null || cameraFront.trim().isEmpty()) {
                model.addAttribute("errorMessage", "Camera trước không được để trống");
                hasError = true;
            }
            if (cpu == null || cpu.trim().isEmpty()) {
                model.addAttribute("errorMessage", "CPU không được để trống");
                hasError = true;
            }
            if (memory == null || memory.trim().isEmpty()) {
                model.addAttribute("errorMessage", "Bộ nhớ không được để trống");
                hasError = true;
            }
            if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
                model.addAttribute("errorMessage", "Danh mục không được để trống");
                hasError = true;
            }
            if (supplierIdStr == null || supplierIdStr.trim().isEmpty()) {
                model.addAttribute("errorMessage", "Nhà cung cấp không được để trống");
                hasError = true;
            }
            if (importQuantityStr == null || importQuantityStr.trim().isEmpty() || Integer.parseInt(importQuantityStr) < 1) {
                model.addAttribute("errorMessage", "Số lượng nhập phải lớn hơn 0");
                hasError = true;
            }
            if (costStr == null || costStr.trim().isEmpty() || Double.parseDouble(costStr) <= 9999) {
                model.addAttribute("errorMessage", "Giá nhập phải lớn hơn hoặc bằng 10,000 VNĐ");
                hasError = true;
            }
            if (hasError) {
                StorageImportProductDTO productTemp = new StorageImportProductDTO();
                productTemp.setProductName(newProductName);
                productTemp.setSize(size);
                productTemp.setCameraBack(cameraBack);
                productTemp.setCameraFront(cameraFront);
                productTemp.setCpu(cpu);
                productTemp.setMemory(memory);
                if (categoryIdStr != null && !categoryIdStr.isEmpty()) productTemp.setCategoryId(Integer.parseInt(categoryIdStr));
                if (supplierIdStr != null && !supplierIdStr.isEmpty()) productTemp.setSupplierId(Integer.parseInt(supplierIdStr));
                model.addAttribute("inforStorages", storageService.findAllStorage());
                model.addAttribute("suppliers", supplierService.findAll());
                model.addAttribute("categorys", categoryRepository.findAll());
                model.addAttribute("productTemp", productTemp);
                model.addAttribute("showAddProductModal", true);
                model.addAttribute("currentPage", "storage");
                return "storage/import-storage";
            }
            StorageImportProductDTO productDTO = new StorageImportProductDTO();
            productDTO.setProductName(newProductName);
            productDTO.setSize(size);
            productDTO.setCameraBack(cameraBack);
            productDTO.setCameraFront(cameraFront);
            productDTO.setCpu(cpu);
            productDTO.setMemory(memory);
            productDTO.setCategoryId(Integer.parseInt(categoryIdStr));
            productDTO.setSupplierId(Integer.parseInt(supplierIdStr));
            productDTO.setImageFiles(imageFiles);
            Product newProduct = productService.createProductFromImportReturnProduct(productDTO, imageFiles);
            StorageImportDTO importDTO = new StorageImportDTO();
            importDTO.setProductId(newProduct.getProductId());
            importDTO.setProductName(newProduct.getProductName());
            importDTO.setSupplierId(newProduct.getSupplier().getSuplierId());
            importDTO.setSupplierName(newProduct.getSupplier().getSuplierName());
            importDTO.setImportQuantity(Integer.parseInt(importQuantityStr));
            importDTO.setCost(Double.parseDouble(costStr));
            storageService.importProduct(importDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm mới và nhập kho thành công!");
            return "redirect:/storage/list-import";
        } else {
            if (result.hasErrors()) {
                model.addAttribute("storageImportDTO", dto);
                model.addAttribute("inforStorages", storageService.findAllStorage());
                model.addAttribute("suppliers", supplierService.findAll());
                model.addAttribute("categorys", categoryRepository.findAll());
                model.addAttribute("productDTO", new StorageImportProductDTO());
                model.addAttribute("currentPage", "storage");
            return "storage/import-storage";
        }
        try {
            storageService.importProduct(dto);
                redirectAttributes.addFlashAttribute("successMessage", "Nhập kho thành công!");
                return "redirect:/storage/list-import";
        } catch (RuntimeException e) {
                model.addAttribute("storageImportDTO", dto);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("inforStorages", storageService.findAllStorage());
            model.addAttribute("suppliers", supplierService.findAll());
                model.addAttribute("categorys", categoryRepository.findAll());
                model.addAttribute("productDTO", new StorageImportProductDTO());
            return "storage/import-storage";
            }
        }
    }

    @PostMapping("/create-product")
    public String createProduct(@Valid @ModelAttribute("productDTO") StorageImportProductDTO productDTO,
                               BindingResult result, 
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("productDTO", productDTO);
            model.addAttribute("currentPage", "storage");
            model.addAttribute("showAddProductModal", true);
            model.addAttribute("inforStorages", storageService.findAllStorage());
            model.addAttribute("suppliers", supplierService.findAll());
            model.addAttribute("categorys", categoryRepository.findAll());
            model.addAttribute("storageImportDTO", new StorageImportDTO());
            return "storage/import-storage";
        }
        try {
            productService.createProductFromImport(productDTO);
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
        model.addAttribute("currentPage", "storage");
        return "storage/export-form";
    }

    @GetMapping("/products")
    public String showProductSelection(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<Storage> storagePage = storageService.searchProductsInStorage(keyword, PageRequest.of(page, size, Sort.by("storageId").descending()));
        
        // Truyền Storage objects thay vì Product để có thể truy cập storageId
        model.addAttribute("storages", storagePage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", storagePage.getTotalPages());
        model.addAttribute("currentPage", "storage");
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

    @GetMapping("/storage/{id}")
    @ResponseBody
    public StorageExportDTO getStorageInfo(@PathVariable Integer id) {
        Storage storage = storageService.getStorageById(id);
        Product product = storage.getProduct();

        return new StorageExportDTO(
                storage.getStorageId(),
                product.getProductId(),
                product.getProductName(),
                product.getSupplier() != null ? product.getSupplier().getSuplierName() : "N/A",
                storage.getQuantity() != null ? storage.getQuantity() : 0,
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
        model.addAttribute("currentPage", "storage");
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
            model.addAttribute("currentPage", "storage");
            model.addAttribute("suppliers", supplierService.findAll());
            model.addAttribute("employees", employeeService.findAll());

            return "storage/edit-import-storage";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/storage/list-import";
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
                model.addAttribute("currentPage", "storage");
                model.addAttribute("suppliers", supplierService.findAll());
                model.addAttribute("employees", employeeService.findAll());
                model.addAttribute("error", "Vui lòng kiểm tra lại dữ liệu nhập vào");
                return "storage/edit-import-storage";
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/storage/list-import";
            }
        }

        try {
            storageService.updateStorage(id, storageDto);
            redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin nhập kho thành công");
            return "redirect:/storage/list-import";
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
                return "redirect:/storage/list-import";
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
        // Sắp xếp theo storageId giảm dần (mới nhất lên đầu)
        Pageable pageable = PageRequest.of(page, size, Sort.by("storageId").descending());
        Page<Storage> storages;
        boolean isSearch = false;

        if (productName != null && !productName.isEmpty()) {
            // Tìm kiếm với sắp xếp giảm dần
            storages = storageService.searchProductsInStorage(productName, pageable);
            isSearch = true;
        } else {
            // Lấy tất cả với sắp xếp giảm dần
            storages = storageService.getAllStorageRecords(pageable);
        }

        // Chuyển đổi Storage thành ProductDTO để tương thích với frontend
        List<Map<String, Object>> products = storages.getContent().stream()
            .map(storage -> {
                Map<String, Object> product = new HashMap<>();
                product.put("storageId", storage.getStorageId());
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