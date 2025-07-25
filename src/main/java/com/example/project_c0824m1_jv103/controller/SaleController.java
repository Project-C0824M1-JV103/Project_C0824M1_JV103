package com.example.project_c0824m1_jv103.controller;

import com.example.project_c0824m1_jv103.controller.Admin.BaseAdminController;
import com.example.project_c0824m1_jv103.dto.CustomerSaleDto;
import com.example.project_c0824m1_jv103.dto.PendingSaleDto;
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
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        Page<Customer> customers = customerService.findAll(PageRequest.of(0, 6, Sort.by("customerId").descending()));
        Page<ProductDTO> products = productService.findAllWithQuantityAndPrice(PageRequest.of(0, 6, Sort.by("productId").descending()));

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
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").descending());
        Page<ProductDTO> products;
        boolean isSearch = false;

        if ((productName != null && !productName.isEmpty())) {
//            products = productService.searchProducts(productName, "productName", pageable);
            products = productService.searchProducts(productName, null,null,1,null, pageable);
            isSearch = true;
        } else {
            products = productService.findAllWithQuantityAndPrice(pageable);
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
    public String showConfirmation(@PathVariable Integer saleId, Model model, Principal principal) {
        Sale sale = saleService.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
        
        model.addAttribute("sale", sale);
        model.addAttribute("currentPage", "sale");
        return "sale/sale-confirmation";
    }

    @GetMapping("/history")
    public String showSaleHistory(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "saleDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Sale> salePage = saleService.findAll(pageable);

        model.addAttribute("salePage", salePage);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("currentPage", "business");

        return "sale/sale-history";
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
                             Model model,
                             HttpSession session) {
        if(bindingResult.hasErrors()) {
            Page<Customer> customers = customerService.findAll(PageRequest.of(0, 6));
            Page<ProductDTO> products = productService.findAll(PageRequest.of(0, 6));

            model.addAttribute("customers", customers.getContent());
            model.addAttribute("products", products);
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "sale/sale-page";
        }
        
        try {
            // chuẩn bị dữ liệu (không lưu vào DB)
            Employee employee = employeeService.findByEmail(saleDto.getEmployeeName());
            if (employee == null) {
                return "redirect:/Sale?error=employee_not_found";
            }

            // Tìm hoặc tạo mới khách hàng (chỉ validate, chưa lưu)
            Customer customer = customerService.findByPhone(saleDto.getPhoneNumber())
                    .orElseGet(() -> {
                        // Validate email trước khi tạo customer mới
                        if (saleDto.getEmail() != null && !saleDto.getEmail().trim().isEmpty()) {
                            if (customerService.isEmailExists(saleDto.getEmail())) {
                                throw new RuntimeException("Email này đã được sử dụng trong hệ thống !");
                            }
                        }
                        
                        Customer newCustomer = new Customer();
                        newCustomer.setCustomerName(saleDto.getCustomerName());
                        newCustomer.setPhoneNumber(saleDto.getPhoneNumber());
                        newCustomer.setAddress(saleDto.getAddress());
                        newCustomer.setEmail(saleDto.getEmail());
                        if (saleDto.getBirthdayDate() != null && !saleDto.getBirthdayDate().isEmpty()) {
                            newCustomer.setBirthdayDate(LocalDate.parse(saleDto.getBirthdayDate()));
                        }
                        return newCustomer; // Chỉ tạo object, chưa lưu
                    });

            // Validate sản phẩm
            List<SaleDetails> saleDetailsList = new ArrayList<>();
            for (SaleDetailsDto productInfo : saleDto.getProducts()) {
                Product product = productService.findProductByName(productInfo.getProductName());
                if (product == null) {
                    return "redirect:/Sale?error=product_not_found";
                }
                // Kiểm tra tồn kho
                if (product.getQuantity() < productInfo.getQuantity()) {
                    return "redirect:/Sale?error=insufficient_stock";
                }
            }

            // Xử lý theo phương thức thanh toán
            if ("VNPAY".equals(saleDto.getPaymentMethod())) {
                // Tạo session data để lưu thông tin đơn hàng tạm thời
                String sessionKey = "pending_sale_" + System.currentTimeMillis();
                // Lưu thông tin đơn hàng vào session hoặc cache
                savePendingSaleToSession(sessionKey, saleDto, employee, customer, session);
                
                try {
                    String paymentUrl = vnPayService.createPaymentUrl(
                        sessionKey, // Sử dụng session key thay vì sale ID
                        BigDecimal.valueOf(saleDto.getAmount()).longValue(),
                        saleDto.isPrintPDF()
                    );
                    return "redirect:" + paymentUrl;
                } catch (UnsupportedEncodingException e) {
                    return "redirect:/Sale?error=vnpay_error";
                }
            } else {
                // Thanh toán tiền mặt
                return processCashPayment(saleDto, employee, customer);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/Sale?error=system_error";
        }
    }

    // Method xử lý thanh toán tiền mặt
    private String processCashPayment(SaleDto saleDto, Employee employee, Customer customer) {
        try {
            // Lưu khách hàng nếu là khách hàng mới
            if (customer.getCustomerId() == null) {
                // Validate email trước khi lưu customer mới  
                if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()) {
                    if (customerService.isEmailExists(customer.getEmail())) {
                        throw new RuntimeException("Email này đã được sử dụng trong hệ thống !");
                    }
                }
                customer = customerService.save(customer);
            }

            // Tạo đơn hàng
            Sale sale = new Sale();
            sale.setEmployee(employee);
            sale.setCustomer(customer);
            sale.setSaleDate(LocalDateTime.now());
            sale.setAmount(BigDecimal.valueOf(saleDto.getAmount()));
            sale.setPaymentMethod(saleDto.getPaymentMethod());

            // Tạo chi tiết đơn hàng
            List<SaleDetails> saleDetailsList = new ArrayList<>();
            for (SaleDetailsDto productInfo : saleDto.getProducts()) {
                SaleDetails saleDetails = new SaleDetails();
                Product product = productService.findProductByName(productInfo.getProductName());
                saleDetails.setProduct(product);
                saleDetails.setUniquePrice(productInfo.getPrice());
                saleDetails.setQuantity(productInfo.getQuantity());
                saleDetails.setSale(sale);
                saleDetailsList.add(saleDetails);
            }
            sale.setSaleDetails(saleDetailsList);

            // Lưu đơn hàng
            Sale savedSale = saleService.createSale(sale);

            // Gửi email xác nhận
            emailService.sendOrderConfirmation(savedSale);

            // Redirect đến trang xác nhận
            String redirectUrl = "redirect:/Sale/confirmation/" + savedSale.getSaleId();
            if (saleDto.isPrintPDF()) {
                redirectUrl += "?printPdf=true";
            }
            return redirectUrl;

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/Sale?error=payment_failed";
        }
    }

    // Method lưu thông tin đơn hàng tạm thời
    private void savePendingSaleToSession(String sessionKey, SaleDto saleDto, Employee employee, Customer customer, HttpSession session) {
        PendingSaleDto pendingSale = new PendingSaleDto(saleDto, employee, customer, sessionKey);
        session.setAttribute(sessionKey, pendingSale);
    }

    // Endpoint callback từ VNPAY
    @GetMapping("/vnpay-callback")
    public String vnpayCallback(@RequestParam Map<String, String> queryParams, 
                               HttpSession session) {
        try {
            // Verify VNPAY response
            boolean isValid = vnPayService.verifyPaymentResponse(queryParams);
            
            if (!isValid) {
                return "redirect:/Sale?error=payment_verification_failed";
            }
            
            // Check payment status
            String responseCode = queryParams.get("vnp_ResponseCode");
            if (!"00".equals(responseCode)) {
                return "redirect:/Sale?error=payment_failed";
            }
            
            String sessionKey = queryParams.get("vnp_TxnRef"); // Lấy session key
            
            // Lấy thông tin đơn hàng từ session
            PendingSaleDto pendingSale = (PendingSaleDto) session.getAttribute(sessionKey);
            
            if (pendingSale == null || !pendingSale.isValid()) {
                return "redirect:/Sale?error=session_expired";
            }
            
            // Tạo đơn hàng thực sự
            Sale savedSale = processPendingSale(pendingSale);
            
            // Xóa session data
            session.removeAttribute(sessionKey);
            
            // Check if print PDF is requested
            String orderInfo = queryParams.get("vnp_OrderInfo");
            boolean printPdf = orderInfo != null && orderInfo.contains("printPdf=true");
            
            String redirectUrl = "redirect:/Sale/confirmation/" + savedSale.getSaleId();
            if (printPdf) {
                redirectUrl += "?printPdf=true";
            }
            
            return redirectUrl;
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/Sale?error=payment_verification_failed";
        }
    }

    // Method xử lý đơn hàng từ pending sale
    private Sale processPendingSale(PendingSaleDto pendingSale) {
        try {
            SaleDto saleDto = pendingSale.getSaleDto();
            Employee employee = pendingSale.getEmployee();
            Customer customer = pendingSale.getCustomer();

            // Lưu khách hàng nếu là khách hàng mới
            if (customer.getCustomerId() == null) {
                customer = customerService.save(customer);
            }

            // Tạo đơn hàng
            Sale sale = new Sale();
            sale.setEmployee(employee);
            sale.setCustomer(customer);
            sale.setSaleDate(LocalDateTime.now());
            sale.setAmount(BigDecimal.valueOf(saleDto.getAmount()));
            sale.setPaymentMethod(saleDto.getPaymentMethod());

            // Tạo chi tiết đơn hàng
            List<SaleDetails> saleDetailsList = new ArrayList<>();
            for (SaleDetailsDto productInfo : saleDto.getProducts()) {
                SaleDetails saleDetails = new SaleDetails();
                Product product = productService.findProductByName(productInfo.getProductName());
                saleDetails.setProduct(product);
                saleDetails.setUniquePrice(productInfo.getPrice());
                saleDetails.setQuantity(productInfo.getQuantity());
                saleDetails.setSale(sale);
                saleDetailsList.add(saleDetails);
            }
            sale.setSaleDetails(saleDetailsList);

            // Lưu đơn hàng
            Sale savedSale = saleService.createSale(sale);

            // Gửi email xác nhận
            emailService.sendOrderConfirmation(savedSale);

            return savedSale;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to process pending sale", e);
        }
    }

    // API endpoint để kiểm tra email đã tồn tại
    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@RequestParam("email") String email) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = customerService.isEmailExists(email); // Đã được cập nhật để check toàn hệ thống
        response.put("exists", exists);
        return response;
    }

    @GetMapping("/check-phone")
    @ResponseBody
    public Map<String, Boolean> checkPhone(@RequestParam("phone") String phoneNumber) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = customerService.isPhoneExists(phoneNumber);
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

    // Endpoint xử lý hủy thanh toán
    @GetMapping("/cancel-payment")
    public String cancelPayment(@RequestParam String sessionKey, HttpSession session) {
        try {
            // Xóa session data
            session.removeAttribute(sessionKey);
            return "redirect:/Sale?error=payment_cancelled";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/Sale?error=system_error";
        }
    }

    // Endpoint để cleanup session expired
    @GetMapping("/cleanup-session")
    public String cleanupExpiredSession(@RequestParam String sessionKey, HttpSession session) {
        try {
            PendingSaleDto pendingSale = (PendingSaleDto) session.getAttribute(sessionKey);
            if (pendingSale != null && !pendingSale.isValid()) {
                session.removeAttribute(sessionKey);
            }
            return "redirect:/Sale?error=session_expired";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/Sale?error=system_error";
        }
    }
}
