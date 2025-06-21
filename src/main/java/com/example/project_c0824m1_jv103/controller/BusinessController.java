package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.ProductDTO;
import com.example.project_c0824m1_jv103.dto.ProductRetailDto;
import com.example.project_c0824m1_jv103.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        Page<ProductDTO> products = productService.findAll(PageRequest.of(0, 6));
        model.addAttribute("productRetailDTO", new ProductRetailDto());
        model.addAttribute("products", products);
        model.addAttribute("currentPage", "business");
        return "business/retail-page";
    }
}
