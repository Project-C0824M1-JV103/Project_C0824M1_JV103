package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.CustomerSaleDto;
import com.example.project_c0824m1_jv103.model.Customer;
import com.example.project_c0824m1_jv103.service.customer.ICustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/Customer")
public class CustomerController extends BaseAdminController  {
    @Autowired
    private ICustomerService iCustomerService;

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
            Customer customerEntity = customer.get();
            
            // Convert Customer entity to CustomerSaleDto
            CustomerSaleDto customerDto = new CustomerSaleDto();
            customerDto.setCustomerId(customerEntity.getCustomerId());
            customerDto.setCustomerName(customerEntity.getCustomerName());
            customerDto.setPhoneNumber(customerEntity.getPhoneNumber());
            customerDto.setAddress(customerEntity.getAddress());
            // Convert LocalDate to String
            customerDto.setBirthdayDate(customerEntity.getBirthdayDate() != null ? 
                customerEntity.getBirthdayDate().toString() : null);
            customerDto.setEmail(customerEntity.getEmail());
            
            model.addAttribute("customer", customerDto);
            model.addAttribute("currentPage", "customer");
            return "admin/editCustomer";
        } else {
            return "redirect:/Customer?error=notfound";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateCustomer(@PathVariable("id") Integer id,
                                 @Valid @ModelAttribute("customer") CustomerSaleDto customerDto,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes,
                                 Principal principal) {
        
        // Kiểm tra validation errors
        if (bindingResult.hasErrors()) {
            customerDto.setCustomerId(id);
            model.addAttribute("customer", customerDto);
            model.addAttribute("currentPage", "customer");
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin nhập vào!");
            return "admin/editCustomer";
        }
        
        try {
            Customer customer = new Customer();
            customer.setCustomerId(id);
            customer.setCustomerName(customerDto.getCustomerName());
            customer.setPhoneNumber(customerDto.getPhoneNumber());
            customer.setAddress(customerDto.getAddress());
            // Convert String to LocalDate
            if (customerDto.getBirthdayDate() != null && !customerDto.getBirthdayDate().trim().isEmpty()) {
                customer.setBirthdayDate(LocalDate.parse(customerDto.getBirthdayDate()));
            } else {
                customer.setBirthdayDate(null);
            }
            customer.setEmail(customerDto.getEmail());
            
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
