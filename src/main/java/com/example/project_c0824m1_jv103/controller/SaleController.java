package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.CustomerSaleDto;
import com.example.project_c0824m1_jv103.dto.ProductDTO;
import com.example.project_c0824m1_jv103.dto.SaleDetailsDto;
import com.example.project_c0824m1_jv103.dto.SaleDto;
import com.example.project_c0824m1_jv103.model.Customer;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.model.Product;
import com.example.project_c0824m1_jv103.model.Sale;
import com.example.project_c0824m1_jv103.model.SaleDetails;
import com.example.project_c0824m1_jv103.service.EmailService;
import com.example.project_c0824m1_jv103.service.PDFService;
import com.example.project_c0824m1_jv103.service.VNPayService;
import com.example.project_c0824m1_jv103.service.customer.ICustomerService;
import com.example.project_c0824m1_jv103.service.employee.IEmployeeService;
import com.example.project_c0824m1_jv103.service.product.IProductService;
import com.example.project_c0824m1_jv103.service.sale.ISaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
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

    @Autowired
    private PDFService pdfService;

    @Autowired
    private EmailService emailService;

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
        model.addAttribute("currentPage", "sale");

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
        model.addAttribute("currentPage", "sale");

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

        // Chuyển đổi sang DTO để tránh lỗi lặp vô hạn khi trả JSON
        List<CustomerSaleDto> customerDtos = customers.getContent().stream().map(customer -> {
            CustomerSaleDto dto = new CustomerSaleDto();
            dto.setCustomerId(customer.getCustomerId());
            dto.setCustomerName(customer.getCustomerName());
            dto.setPhoneNumber(customer.getPhoneNumber());
            dto.setAddress(customer.getAddress());
            dto.setBirthdayDate(customer.getBirthdayDate() != null ? customer.getBirthdayDate().toString() : null);
            dto.setEmail(customer.getEmail());
            return dto;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("customers", customerDtos);
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

    @GetMapping("/invoice/{saleId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Integer saleId) {
        Sale sale = saleService.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        byte[] pdfBytes = pdfService.generateInvoicePDF(sale);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice_" + saleId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/create")
    public String createSale(@Valid @ModelAttribute("saleDto") SaleDto saleDto,
                             BindingResult bindingResult,
                             Model model) {
        if(bindingResult.hasErrors()) {
            Page<Customer> customers = customerService.findAll(PageRequest.of(0, 6));
            Page<ProductDTO> products = productService.findAll(PageRequest.of(0, 6));

            model.addAttribute("customers", customers.getContent());
            model.addAttribute("products", products);
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "sale/sale-page";
        }
        try {
            Sale sale = new Sale();

            // Tìm nhân viên
            Employee employee = employeeService.findByEmail(saleDto.getEmployeeName());
            if (employee == null) {
                return "redirect:/Sale?error=employee_not_found";
            }

            // Tìm hoặc tạo mới khách hàng
            Customer customer = customerService.findByPhone(saleDto.getPhoneNumber())
                    .orElseGet(() -> {
                        Customer newCustomer = new Customer();
                        newCustomer.setCustomerName(saleDto.getCustomerName());
                        newCustomer.setPhoneNumber(saleDto.getPhoneNumber());
                        newCustomer.setAddress(saleDto.getAddress());
                        newCustomer.setEmail(saleDto.getEmail());
                        if (saleDto.getBirthdayDate() != null && !saleDto.getBirthdayDate().isEmpty()) {
                            newCustomer.setBirthdayDate(LocalDate.parse(saleDto.getBirthdayDate()));
                        }
                        return customerService.save(newCustomer);
                    });

            // Thiết lập thông tin cơ bản của đơn hàng
            sale.setEmployee(employee);
            sale.setCustomer(customer);
            sale.setSaleDate(LocalDateTime.now());
            sale.setAmount(BigDecimal.valueOf(saleDto.getAmount()));
            sale.setPaymentMethod(saleDto.getPaymentMethod());

            // Tạo danh sách chi tiết đơn hàng cho nhiều sản phẩm
            List<SaleDetails> saleDetailsList = new ArrayList<>();
            
            for (SaleDetailsDto productInfo : saleDto.getProducts()) {
                SaleDetails saleDetails = new SaleDetails();
                Product product = productService.findProductByName(productInfo.getProductName());
                if (product == null) {
                    return "redirect:/Sale?error=product_not_found";
                }
                saleDetails.setProduct(product);
                saleDetails.setUniquePrice(productInfo.getPrice());
                saleDetails.setQuantity(productInfo.getQuantity());
                saleDetails.setSale(sale);
                saleDetailsList.add(saleDetails);
            }

            sale.setSaleDetails(saleDetailsList);

            // Lưu đơn hàng vào database
            Sale savedSale = saleService.createSale(sale);

            // Xử lý thanh toán dựa trên phương thức
            if ("VNPAY".equals(saleDto.getPaymentMethod())) {
                try {
                    String paymentUrl = vnPayService.createPaymentUrl(
                        "ORDER_" + savedSale.getSaleId(),
                        savedSale.getAmount().longValue(),
                        saleDto.isPrintPDF()
                    );
                    return "redirect:" + paymentUrl;

                } catch (UnsupportedEncodingException e) {
                    return "redirect:/Sale?error=vnpay_error";
                }
            }

            // Nếu thanh toán tiền mặt, chuyển hướng đến trang xác nhận
            String redirectUrl = "redirect:/Sale/confirmation/" + savedSale.getSaleId();
            if (saleDto.isPrintPDF()) {
                redirectUrl += "?printPdf=true";
            }
            return redirectUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/Sale?error=system_error";
        }
    }

    // API endpoint để kiểm tra email đã tồn tại
    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@RequestParam("email") String email) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = customerService.isEmailExists(email);
        response.put("exists", exists);
        return response;
    }

    @PostMapping("/send-otp")
    @ResponseBody
    public Map<String, Object> sendOtp(@RequestParam String email) {
        Map<String, Object> res = new HashMap<>();
        boolean sent = emailService.sendOtp(email);
        res.put("success", sent);
        res.put("message", sent ? "Đã gửi OTP đến email." : "Không thể gửi OTP. Vui lòng thử lại.");
        return res;
    }

    @PostMapping("/verify-otp")
    @ResponseBody
    public Map<String, Object> verifyOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");
        boolean verified = emailService.verifyOtp(email, otp);
        Map<String, Object> res = new HashMap<>();
        res.put("verified", verified);
        res.put("message", verified ? "Xác thực thành công!" : "Mã OTP không đúng hoặc đã hết hạn.");
        return res;
    }
}
