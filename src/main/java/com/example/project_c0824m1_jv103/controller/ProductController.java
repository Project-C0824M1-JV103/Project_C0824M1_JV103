package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.model.Product;
import com.example.project_c0824m1_jv103.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private IProductService productService;

    @GetMapping("/list")
    public String listProducts(
            Model model,
            @RequestParam(value = "field", required = false, defaultValue = "productName") String field,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page
    ) {
        int pageSize = 6;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Product> productPage;
        if (keyword != null && !keyword.isEmpty()) {
            productPage = productService.searchProducts(keyword, field, pageable);
        } else {
            productPage = productService.findAll(pageable);
        }
        model.addAttribute("productPage", productPage);
        model.addAttribute("field", field);
        model.addAttribute("keyword", keyword);
        return "product/list-product";
    }

    @GetMapping("/add")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/add-product-form";
    }

    // Xử lý form thêm sản phẩm
    @PostMapping("/add-product")
    public String createProduct(
            @ModelAttribute("product") Product product,
            @RequestParam("images") List<MultipartFile> images,
            @RequestParam(value = "captions", required = false) List<String> captions,
            Model model) {
        try {
            productService.createProduct(product, images, captions != null ? captions : new ArrayList<>());
            return "redirect:/product/list"; // Chuyển hướng sau khi thành công
        } catch (IOException e) {
            model.addAttribute("error", "Lỗi khi upload ảnh: " + e.getMessage());
            return "product/add-product-form"; // Quay lại form nếu có lỗi
        }
    }

}
