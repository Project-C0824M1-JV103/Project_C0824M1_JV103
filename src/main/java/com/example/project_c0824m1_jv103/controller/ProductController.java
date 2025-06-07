package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.dto.ProductDTO;
import com.example.project_c0824m1_jv103.service.product.IProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/Admin/product")
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
        Page<ProductDTO> productPage;
        
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
        model.addAttribute("productDTO", new ProductDTO());
        return "product/add-product-form";
    }

    @PostMapping("/add-product")
    public String createProduct(
            @Valid @ModelAttribute("productDTO") ProductDTO productDTO,
            BindingResult bindingResult,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "captions", required = false) List<String> captions,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("productDTO", productDTO);
            return "product/add-product-form";
        }
        
        try {
            productService.createProduct(productDTO, 
                images != null ? images : new ArrayList<>(), 
                captions != null ? captions : new ArrayList<>());
            
            redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm thành công!");
            return "redirect:/Admin/product/list";
        } catch (IOException e) {
            model.addAttribute("error", "Lỗi khi upload ảnh: " + e.getMessage());
            model.addAttribute("productDTO", productDTO);
            return "product/add-product-form";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
            model.addAttribute("productDTO", productDTO);
            return "product/add-product-form";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ProductDTO productDTO = productService.findById(id);
            if (productDTO != null) {
                model.addAttribute("productDTO", productDTO);
                return "product/edit-product-form";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm!");
                return "redirect:/Admin/product/list";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tải thông tin sản phẩm: " + e.getMessage());
            return "redirect:/Admin/product/list";
        }
    }

    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("productDTO") ProductDTO productDTO,
            BindingResult bindingResult,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "captions", required = false) List<String> captions,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            productDTO.setProductId(id.intValue());
            model.addAttribute("productDTO", productDTO);
            return "product/edit-product-form";
        }
        
        try {
            productService.updateProduct(id, productDTO, 
                images != null ? images : new ArrayList<>(), 
                captions != null ? captions : new ArrayList<>());
            
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sản phẩm thành công!");
            return "redirect:/Admin/product/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            productDTO.setProductId(id.intValue());
            model.addAttribute("productDTO", productDTO);
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
}
