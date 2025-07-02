package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.ProductDTO;
import com.example.project_c0824m1_jv103.dto.ProductRetailDto;
import com.example.project_c0824m1_jv103.model.Product;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/Business")
public class BusinessController extends BaseAdminController {

    @Autowired
    private IProductService productService;

    @GetMapping("")
    public String BusinessPage(Model model) {
        model.addAttribute("currentPage", "business");
        return "business/business-page";
    }

    @GetMapping("/retail")
    public String RetailPage(Model model) {
        Page<ProductDTO> products = productService.findAllWithQuantity(PageRequest.of(0, 6, Sort.by("productId").descending()));
        model.addAttribute("productRetailDto", new ProductRetailDto());
        model.addAttribute("products", products);
        model.addAttribute("currentPage", "business");
        return "business/retail-page";
    }

    @GetMapping("/products/data")
    @ResponseBody
    public Map<String, Object> getProductsData(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").descending());
        Page<ProductDTO> products;
        boolean isSearch = false;

        if (productName != null && !productName.isEmpty()) {
            if ("zeroPrice".equals(filter)) {
                products = productService.searchProductsWithQuantityAndZeroPrice(productName, pageable);
            } else {
                products = productService.searchProductsWithQuantity(productName, pageable);
            }
            isSearch = true;
        } else {
            if ("zeroPrice".equals(filter)) {
                products = productService.findAllWithQuantityAndZeroPrice(pageable);
            } else {
                products = productService.findAllWithQuantity(pageable);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("products", products.getContent());
        response.put("pageNumber", page);
        response.put("pageSize", size);
        response.put("totalPages", products.getTotalPages());
        response.put("totalElements", products.getTotalElements());
        response.put("isSearch", isSearch);

        return response;
    }

    @GetMapping("/products/{id}")
    @ResponseBody
    public ProductDTO getProduct(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping("/retail/update")
    public String updateRetail(@RequestParam(value = "productId", required = false) Integer id,
                               @Valid @ModelAttribute("productRetailDto") ProductRetailDto productRetailDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if( id == null ) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn sản phẩm!");
            return "redirect:/Business/retail";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentPage", "business");
            return "business/retail-page";
        }
        Product product = productService.findProductById(id);
        product.setPrice(productRetailDto.getRetailPrice());
        productService.updateProductRetail(product);
        redirectAttributes.addFlashAttribute("message", "Cập nhật giá bán lẻ của " + productRetailDto.getProductName() + " thành công!" );
        return "redirect:/Business/retail";
    }
}
