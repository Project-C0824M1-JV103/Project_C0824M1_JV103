package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.model.Customer;
import com.example.project_c0824m1_jv103.service.customer.ICustomerService;
import com.example.project_c0824m1_jv103.service.sale.ISaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/Customer")
public class CustomerController extends BaseAdminController  {
    @Autowired
    private ICustomerService iCustomerService;
    
    @Autowired
    private ISaleService saleService;

    @GetMapping()
    public String showListCustomer(Model model,
                                   Principal principal,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "6") int size,
                                   @RequestParam(required = false) String customerName,
                                   @RequestParam(required = false) String phoneNumber) {

        if (page < 0) page = 0;
        if (size <= 0) size = 6;

        Pageable pageable = PageRequest.of(page, size, Sort.by("customerId").descending());
        Page<Customer> customerPage;
        boolean isSearch = false;

        String trimmedName = StringUtils.hasText(customerName) ? customerName.trim() : null;
        String trimmedPhone = StringUtils.hasText(phoneNumber) ? phoneNumber.trim() : null;

        if (trimmedName != null || trimmedPhone != null) {
            // Perform search using Java backend
            customerPage = iCustomerService.searchByNameAndPhone(trimmedName, trimmedPhone, pageable);
            isSearch = true;



        } else {

            customerPage = iCustomerService.findAll(pageable);
        }

        // Add model attributes
        model.addAttribute("customerPage", customerPage);
        model.addAttribute("customers", customerPage.getContent());
        model.addAttribute("currentPage", "customer");
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("totalElements", customerPage.getTotalElements());
        model.addAttribute("pageNumber", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("customerName", trimmedName);
        model.addAttribute("phoneNumber", trimmedPhone);
        model.addAttribute("isSearch", isSearch);

        // Add search statistics
        if (isSearch) {
            model.addAttribute("searchResultsCount", customerPage.getTotalElements());
            model.addAttribute("hasResults", customerPage.getTotalElements() > 0);
        }

        return "admin/showListCustomer";
    }

    @GetMapping("/edit/{id}")
    public String showEditCustomerForm(@PathVariable("id") Integer id, Model model, Principal principal) {
        Optional<Customer> customer = iCustomerService.findById(id);
        if (customer.isPresent()) {
            Customer customerObj = customer.get();
            
            // Đếm số đơn hàng của customer từ database
            Long totalOrders = saleService.countSalesByCustomerId(id);
            
            model.addAttribute("customer", customerObj);
            model.addAttribute("totalOrders", totalOrders);
            model.addAttribute("currentPage", "customer");
            return "admin/editCustomer";
        } else {
            return "redirect:/Customer?error=notfound";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateCustomer(@PathVariable("id") Integer id,
                                 @ModelAttribute Customer customer,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 Principal principal) {
        try {
            // Validation for duplicate email across all tables (Customer, Employee, Supplier)
            if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()) {
                if (iCustomerService.isEmailExistsForOtherCustomer(customer.getEmail(), id)) {
                    model.addAttribute("customer", customer);
                    model.addAttribute("currentPage", "customer");
                    model.addAttribute("errorMessage", "Email này đã được sử dụng trong hệ thống !");
                    return "admin/editCustomer";
                }
            }

            // Validation for duplicate phone number
            if (customer.getPhoneNumber() != null && !customer.getPhoneNumber().trim().isEmpty()) {
                if (iCustomerService.isPhoneExistsForOtherCustomer(customer.getPhoneNumber(), id)) {
                    model.addAttribute("customer", customer);
                    model.addAttribute("currentPage", "customer");
                    model.addAttribute("errorMessage", "Số điện thoại này đã được sử dụng !");
                    return "admin/editCustomer";
                }
            }

            customer.setCustomerId(id);
            iCustomerService.save(customer);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật thông tin khách hàng!");
        }
        return "redirect:/Customer";
    }

    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            iCustomerService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa khách hàng này!");
        }
        return "redirect:/Customer";
    }
    }
