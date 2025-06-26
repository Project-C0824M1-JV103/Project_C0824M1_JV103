package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.ProductDTO;
import com.example.project_c0824m1_jv103.service.product.IProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/product")
public class ProductController extends BaseAdminController {

    @Autowired
    private IProductService productService;

    @GetMapping("")
    public String listProducts(
            Model model,
            Principal principal,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "minQuantity", required = false) Integer minQuantity,
            @RequestParam(value = "maxQuantity", required = false) Integer maxQuantity,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page
    ) {
        int pageSize = 6;
        // Sắp xếp theo productId giảm dần để sản phẩm mới nhất hiển thị đầu tiên
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "productId"));
        Page<ProductDTO> productPage;
        
        // Kiểm tra xem có điều kiện tìm kiếm nào không
        boolean hasSearchCriteria = productName != null || minPrice != null || maxPrice != null
                                || minQuantity != null || maxQuantity != null;

        if (hasSearchCriteria) {
            productPage = productService.searchProducts(
                productName,
                minPrice,
                maxPrice,
                minQuantity,
                maxQuantity,
                pageable
            );
        } else {
            productPage = productService.findAll(pageable);
        }

        // Thêm các thuộc tính vào model
        model.addAttribute("productPage", productPage);
        model.addAttribute("productName", productName);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("minQuantity", minQuantity);
        model.addAttribute("maxQuantity", maxQuantity);
        model.addAttribute("currentPage", "product");

        // Thêm thông tin tìm kiếm
        if (hasSearchCriteria) {
            model.addAttribute("searchResultsCount", productPage.getTotalElements());
            model.addAttribute("hasResults", productPage.getTotalElements() > 0);
        }

        return "product/list-product";
    }

    @GetMapping("/add")
    public String showCreateProductForm(Model model, Principal principal) {
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("suppliers", productService.getAllSuppliers());
        model.addAttribute("currentPage", "product");
        return "product/add-product-form";
    }

    @PostMapping("/add-product")
    public String createProduct(
            @Valid @ModelAttribute("productDTO") ProductDTO productDTO,
            BindingResult bindingResult,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> images,
            @RequestParam(value = "captions", required = false) List<String> captions,
            @RequestParam(value = "deletedImageUrls", required = false) String deletedImageUrls,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validate images
        String imageValidationError = validateImages(images);
        if (imageValidationError != null) {
            model.addAttribute("error", imageValidationError);
            model.addAttribute("productDTO", productDTO);
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("suppliers", productService.getAllSuppliers());
            return "product/add-product-form";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("productDTO", productDTO);
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("suppliers", productService.getAllSuppliers());
            return "product/add-product-form";
        }

        try {
            // Chuyển string thành list
            List<String> deletedUrls = new ArrayList<>();
            if (deletedImageUrls != null && !deletedImageUrls.trim().isEmpty()) {
                deletedUrls = Arrays.asList(deletedImageUrls.split(","));
            }
            productService.createProduct(productDTO,
                    images != null ? images : new ArrayList<>(),
                    captions != null ? captions : new ArrayList<>());

            redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm thành công!");
            return "redirect:/product";
        } catch (IOException e) {
            model.addAttribute("error", "Lỗi khi upload ảnh: " + e.getMessage());
            model.addAttribute("productDTO", productDTO);
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("suppliers", productService.getAllSuppliers());
            return "product/add-product-form";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
            model.addAttribute("productDTO", productDTO);
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("suppliers", productService.getAllSuppliers());
            return "product/add-product-form";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            ProductDTO productDTO = productService.findById(id);
            if (productDTO != null) {
                model.addAttribute("productDTO", productDTO);
                model.addAttribute("categories", productService.getAllCategories());
                model.addAttribute("suppliers", productService.getAllSuppliers());
                model.addAttribute("currentPage", "product");
                return "product/edit-product-form";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm!");
                return "redirect:/product";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tải thông tin sản phẩm: " + e.getMessage());
            return "redirect:/product";
        }
    }

    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("productDTO") ProductDTO productDTO,
            BindingResult bindingResult,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> images,
            @RequestParam(value = "captions", required = false) List<String> captions,
            @RequestParam(value = "deletedImageUrls", required = false) String deletedImageUrls,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Debug logs
//        System.out.println("Received update request for product ID: " + id);
//        System.out.println("Number of new images: " + (images != null ? images.size() : 0));
//        System.out.println("Deleted image URLs: " + deletedImageUrls);

        // Validate new images
        String imageValidationError = validateImages(images);
        if (imageValidationError != null) {
            model.addAttribute("error", imageValidationError);
            productDTO.setProductId(id.intValue());
            model.addAttribute("productDTO", productDTO);
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("suppliers", productService.getAllSuppliers());
            return "product/edit-product-form";
        }

        if (bindingResult.hasErrors()) {
            productDTO.setProductId(id.intValue());
            model.addAttribute("productDTO", productDTO);
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("suppliers", productService.getAllSuppliers());
            return "product/edit-product-form";
        }
        
        try {
            // Chuyển string thành list
            List<String> deletedUrls = new ArrayList<>();
            if (deletedImageUrls != null && !deletedImageUrls.trim().isEmpty()) {
                deletedUrls = Arrays.asList(deletedImageUrls.split(","));

            }

            // Debug log existing images
            ProductDTO existingProduct = productService.findById(id);

            // Validate tổng số ảnh sau khi cập nhật
            int existingImageCount = existingProduct.getExistingImageUrls() != null ?
                    existingProduct.getExistingImageUrls().size() : 0;
            int newImageCount = images != null ? (int) images.stream().filter(file -> !file.isEmpty()).count() : 0;
            int deletedImageCount = deletedUrls.size();
            int totalImagesAfterUpdate = existingImageCount - deletedImageCount + newImageCount;

//            System.out.println("Total images calculation:");
//            System.out.println("- Existing images: " + existingImageCount);
//            System.out.println("- New images: " + newImageCount);
//            System.out.println("- Deleted images: " + deletedImageCount);
//            System.out.println("- Total after update: " + totalImagesAfterUpdate);

            if (totalImagesAfterUpdate > 4) {
                model.addAttribute("error", "Tổng số ảnh không được vượt quá 4 ảnh. Hiện tại: " + totalImagesAfterUpdate);
                productDTO.setProductId(id.intValue());
                model.addAttribute("productDTO", productDTO);
                model.addAttribute("categories", productService.getAllCategories());
                model.addAttribute("suppliers", productService.getAllSuppliers());
                return "product/edit-product-form";
            }

            // Debug log before service call
            if (images != null) {
                images.forEach(file -> {
                    if (!file.isEmpty()) {
                        System.out.println("Processing new image: " + file.getOriginalFilename() + 
                            ", size: " + file.getSize() + " bytes");
                    }
                });
            }

            productService.updateProduct(id, productDTO,
                    images != null ? images : new ArrayList<>(),
                    captions != null ? captions : new ArrayList<>(),
                    deletedUrls);

            // Debug log after update
//            ProductDTO updatedProduct = productService.findById(id);
//            System.out.println("Images after update: " +
//                (updatedProduct.getExistingImageUrls() != null ? updatedProduct.getExistingImageUrls() : "none"));

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sản phẩm thành công!");
            return "redirect:/product";
        } catch (Exception e) {
            System.err.println("Error updating product: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            productDTO.setProductId(id.intValue());
            model.addAttribute("productDTO", productDTO);
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("suppliers", productService.getAllSuppliers());
            return "product/edit-product-form";
        }
    }

//    @GetMapping("/delete/{id}")
//    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
//        try {
//            productService.deleteProduct(id);
//            redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm thành công!");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa sản phẩm: " + e.getMessage());
//        }
//        return "redirect:/product/list";
//    }
//
//    @GetMapping("/detail/{id}")
//    public String viewProductDetail(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
//        try {
//            ProductDTO productDTO = productService.findById(id);
//            if (productDTO != null) {
//                model.addAttribute("productDTO", productDTO);
//                return "product/product-detail";
//            } else {
//                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm!");
//                return "redirect:/product/list";
//            }
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tải thông tin sản phẩm: " + e.getMessage());
//            return "redirect:/product/list";
//        }
//    }


    private String validateImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return null; // No images to validate
        }

        // Filter out empty files
        List<MultipartFile> nonEmptyImages = images.stream()
                .filter(file -> file != null && !file.isEmpty())
                .toList();

        // Check maximum number of images
        if (nonEmptyImages.size() > 4) {
            return "Chỉ được chọn tối đa 4 ảnh. Bạn đã chọn: " + nonEmptyImages.size() + " ảnh.";
        }

        // Check for duplicate file names
        Set<String> fileNames = new HashSet<>();
        for (MultipartFile file : nonEmptyImages) {
            String fileName = file.getOriginalFilename();
            if (fileName != null) {
                if (fileNames.contains(fileName)) {
                    return "Không được chọn ảnh trùng nhau. Ảnh '" + fileName + "' đã được chọn.";
                }
                fileNames.add(fileName);
            }
        }


        for (MultipartFile file : nonEmptyImages) {
            if (file.getSize() > 5 * 1024 * 1024) { // 5MB in bytes
                return "Kích thước file ảnh '" + file.getOriginalFilename() + "' không được vượt quá 5MB.";
            }
        }


        for (MultipartFile file : nonEmptyImages) {
            String contentType = file.getContentType();
            if (contentType == null ||
                    (!contentType.startsWith("image/jpeg") &&
                            !contentType.startsWith("image/png") &&
                            !contentType.startsWith("image/gif"))) {
                return "Chỉ chấp nhận file ảnh định dạng JPG, PNG, GIF. File '" +
                        file.getOriginalFilename() + "' không hợp lệ.";
            }
        }

        return null;
    }
}
