//package com.example.project_c0824m1_jv103.controller;
//
//import com.example.project_c0824m1_jv103.model.Product;
//import com.example.project_c0824m1_jv103.service.product.IProductService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//@Controller
//@RequestMapping("/product")
//public class ProductController {
//
//    @Autowired
//    private IProductService productService;
//
//    @GetMapping("/list")
//    public String showCreateProductForm(Model model) {
//        model.addAttribute("product", new Product());
//        return "product/add-product-form";
//    }
//
//    // Xử lý form thêm sản phẩm
//    @PostMapping("/add-product")
//    public String createProduct(
//            @ModelAttribute("product") Product product,
//            @RequestParam("images") List<MultipartFile> images,
//            @RequestParam(value = "captions", required = false) List<String> captions,
//            Model model) {
//        try {
//            productService.createProduct(product, images, captions != null ? captions : new ArrayList<>());
//            return "redirect:/product/list"; // Chuyển hướng sau khi thành công
//        } catch (IOException e) {
//            model.addAttribute("error", "Lỗi khi upload ảnh: " + e.getMessage());
//            return "product/add-product-form"; // Quay lại form nếu có lỗi
//        }
//    }
//
//}
package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.model.Product;

import com.example.project_c0824m1_jv103.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductController {

    @Autowired
    private IProductService productService;

    // Hiển thị form thêm sản phẩm
    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("suppliers", productService.getAllSuppliers());
        return "product/add";
    }

    // Xử lý thêm sản phẩm
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute("product") Product product,
                              @RequestParam("images") MultipartFile[] images,
                              RedirectAttributes redirectAttributes) {
        try {
            productService.saveProduct(product, images);
            redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
        }
        return "redirect:/products/add";
    }
}
