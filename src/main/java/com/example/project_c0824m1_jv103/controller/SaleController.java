package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.CustomerSaleDto;
import com.example.project_c0824m1_jv103.dto.ProductDTO;
import com.example.project_c0824m1_jv103.dto.SaleDetailsDto;
import com.example.project_c0824m1_jv103.dto.SaleDto;
import com.example.project_c0824m1_jv103.model.Customer;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.model.Sale;
import com.example.project_c0824m1_jv103.model.SaleDetails;
import com.example.project_c0824m1_jv103.service.VNPayService;
import com.example.project_c0824m1_jv103.service.customer.ICustomerService;
import com.example.project_c0824m1_jv103.service.employee.IEmployeeService;
import com.example.project_c0824m1_jv103.service.product.IProductService;
import com.example.project_c0824m1_jv103.service.sale.ISaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/Sale")
public class SaleController extends BaseAdminController {
    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IProductService productService;

    @Autowired
    private ISaleService saleService;

    @Autowired
    private VNPayService vnPayService;

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

    @GetMapping("/products/data")
    @ResponseBody
    public Map<String, Object> getProductsData(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> products;
        boolean isSearch = false;

        if ((productName != null && !productName.isEmpty())) {
            products = productService.searchProducts(productName, "productName", pageable);
            isSearch = true;
        } else {
            products = productService.findAll(pageable);
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

    @GetMapping("/confirmation/{saleId}")
    public String showConfirmation(@PathVariable Integer saleId, Model model) {
        Sale sale = saleService.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
        
        model.addAttribute("sale", sale);
        return "sale/sale-confirmation";
    }

    @PostMapping("/create")
    public String createSale(@ModelAttribute("saleDto") SaleDto saleDto) {
        try {
            Sale sale = new Sale();

            Employee employee = employeeService.findByEmail(saleDto.getEmployeeName());

            Customer customer = customerService.findByPhone(saleDto.getPhoneNumber())
                    .orElseGet(() -> {
                        Customer newCustomer = new Customer();
                        newCustomer.setCustomerName(saleDto.getCustomerName());
                        newCustomer.setPhoneNumber(saleDto.getPhoneNumber());
                        newCustomer.setAddress(saleDto.getAddress());
                        newCustomer.setEmail(saleDto.getEmail());
                        newCustomer.setBirthdayDate(LocalDate.parse(saleDto.getBirthdayDate()));
                        return customerService.save(newCustomer);
                    });

            sale.setEmployee(employee);
            sale.setCustomer(customer);
            sale.setSaleDate(LocalDateTime.now());
            sale.setAmount(saleDto.getAmount());
            sale.setPaymentMethod(saleDto.getPaymentMethod());

            // Tạo danh sách SaleDetails
            List<SaleDetails> saleDetailsList = new ArrayList<>();
            if (saleDto.getSaleDetails() != null) {
                for (SaleDetailsDto detailDto : saleDto.getSaleDetails()) {
                    SaleDetails detail = new SaleDetails();
                    detail.setProduct(productService.findProductById(Long.valueOf(detailDto.getProductId())));
                    detail.setQuantity(detailDto.getQuantity());
                    detail.setUniquePrice(detailDto.getUniquePrice());
                    detail.setSale(sale);
                    saleDetailsList.add(detail);
                }
            }
            sale.setSaleDetails(saleDetailsList);

            // Lưu Sale vào database
            Sale savedSale = saleService.createSale(sale);

            // Nếu thanh toán bằng VNPay, tạo URL thanh toán
            if ("VNPAY".equals(saleDto.getPaymentMethod())) {
                String paymentUrl = vnPayService.createPaymentUrl(
                    "ORDER_" + savedSale.getSaleId(),
                    savedSale.getAmount().longValue()
                );
                return "redirect:" + paymentUrl;
            }

            // Nếu thanh toán tiền mặt, chuyển hướng đến trang xác nhận
            return "redirect:/Sale/confirmation/" + savedSale.getSaleId();
        } catch (UnsupportedEncodingException e) {
            // Xử lý lỗi khi tạo URL thanh toán VNPay
            return "redirect:/Sale?error=payment_error";
        } catch (Exception e) {
            // Xử lý các lỗi khác
            return "redirect:/Sale?error=system_error";
        }
    }
}
