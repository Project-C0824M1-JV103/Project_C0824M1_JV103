package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.CustomerSaleDto;
import com.example.project_c0824m1_jv103.dto.ProductDTO;
import com.example.project_c0824m1_jv103.dto.SaleDto;
import com.example.project_c0824m1_jv103.model.Customer;
import com.example.project_c0824m1_jv103.service.customer.ICustomerService;
import com.example.project_c0824m1_jv103.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/Admin/Sale")
public class SaleController extends BaseAdminController {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IProductService productService;

    @GetMapping("")
    public String showSalePage(Model model) {
        Page<Customer> customers = customerService.findAll(PageRequest.of(0, 6));
        Page<ProductDTO> products = productService.findAll(PageRequest.of(0, 6));

        model.addAttribute("customers", customers.getContent());
        model.addAttribute("customerName", null);
        model.addAttribute("phoneNumber", null);
        model.addAttribute("pageNumber", 0);
        model.addAttribute("pageSize", 6);
        model.addAttribute("totalPages", customers.getTotalPages());
        model.addAttribute("totalElements", customers.getTotalElements());
        model.addAttribute("isSearch", false);

        model.addAttribute("products", products);
        model.addAttribute("saleDto", new SaleDto());
        return "sale/sale-page";
    }

    @GetMapping("/customers")
    public String showCustomers(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customers;
        boolean isSearch = false;

        if ((customerName != null && !customerName.isEmpty()) ||
                (phoneNumber != null && !phoneNumber.isEmpty())) {
            customers = customerService.searchByNameAndPhone(customerName, phoneNumber, pageable);
            isSearch = true;
        } else {
            customers = customerService.findAll(pageable);
        }

        model.addAttribute("customers", customers.getContent());
        model.addAttribute("customerName", customerName);
        model.addAttribute("phoneNumber", phoneNumber);
        model.addAttribute("pageNumber", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", customers.getTotalPages());
        model.addAttribute("totalElements", customers.getTotalElements());
        model.addAttribute("isSearch", isSearch);

        return "sale/sale-page";
    }

    @GetMapping("/customers/data")
    @ResponseBody
    public Map<String, Object> getCustomersData(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customers;
        boolean isSearch = false;

        if ((customerName != null && !customerName.isEmpty()) ||
                (phoneNumber != null && !phoneNumber.isEmpty())) {
            customers = customerService.searchByNameAndPhone(customerName, phoneNumber, pageable);
            isSearch = true;
        } else {
            customers = customerService.findAll(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("customers", customers.getContent());
        response.put("pageNumber", page);
        response.put("pageSize", size);
        response.put("totalPages", customers.getTotalPages());
        response.put("totalElements", customers.getTotalElements());
        response.put("isSearch", isSearch);

        return response;
    }

    @GetMapping("/customers/{id}")
    @ResponseBody
    public CustomerSaleDto getCustomer(@PathVariable Integer id) {
        return customerService.findById(id)
                .map(customer -> {
                    CustomerSaleDto dto = new CustomerSaleDto();
                    dto.setCustomerId(customer.getCustomerId());
                    dto.setCustomerName(customer.getCustomerName());
                    dto.setPhoneNumber(customer.getPhoneNumber());
                    dto.setAddress(customer.getAddress());
                    dto.setBirthdayDate(customer.getBirthdayDate().toString());
                    dto.setEmail(customer.getEmail());
                    return dto;
                })
                .orElse(null);
    }

    @PostMapping("/create")
    public String createSale(@ModelAttribute("saleDto") SaleDto saleDto) {
        return "redirect:/Admin/Sale";
    }

}
