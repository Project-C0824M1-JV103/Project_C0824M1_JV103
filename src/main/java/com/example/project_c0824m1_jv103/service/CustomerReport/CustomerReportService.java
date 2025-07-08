package com.example.project_c0824m1_jv103.service.CustomerReport;

import com.example.project_c0824m1_jv103.dto.CustomerReportDto;
import com.example.project_c0824m1_jv103.model.Customer;
import com.example.project_c0824m1_jv103.model.Sale;
import com.example.project_c0824m1_jv103.repository.ICustomerRepository;
import com.example.project_c0824m1_jv103.repository.ISaleRepository;
import com.example.project_c0824m1_jv103.service.customer.ICustomerService;
import com.example.project_c0824m1_jv103.service.sale.ISaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerReportService {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private ISaleService saleService;

    @Autowired
    private ICustomerRepository customerRepository;

    @Autowired
    private ISaleRepository saleRepository;

    /**
     * Lấy danh sách báo cáo khách hàng với lọc theo giới tính và tuổi.
     */
    public Page<CustomerReportDto> getCustomerReports(Pageable pageable, String gender, Integer minAge, Integer maxAge) {
        // Lấy tất cả khách hàng
        Page<Customer> customerPage = customerService.findAll(pageable);

        // Lọc và chuyển đổi sang danh sách DTO
        List<CustomerReportDto> reportDtos = customerPage.getContent().stream().filter(customer -> {
            boolean genderMatch = (gender == null || gender.isEmpty() || customer.getGender() != null && customer.getGender().equalsIgnoreCase(gender));
            int age = calculateAge(customer.getBirthdayDate());
            boolean ageMatch = (minAge == null || age >= minAge) && (maxAge == null || age <= maxAge);
            return genderMatch && ageMatch;
        }).map(customer -> {
            // Tính tổng số đơn hàng
            Long totalSales = saleService.countSalesByCustomerId(customer.getCustomerId());
            // Lấy danh sách sale để tính tổng doanh thu
            List<Sale> sales = saleRepository.findByCustomer_CustomerId(customer.getCustomerId());
            BigDecimal totalAmount = sales.stream()
                    .map(Sale::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return new CustomerReportDto(
                    customer.getCustomerId(),
                    customer.getCustomerName(),
                    customer.getPhoneNumber(),
                    customer.getEmail(),
                    totalSales != null ? totalSales.intValue() : 0,
                    totalAmount,
                    calculateAge(customer.getBirthdayDate()),
                    customer.getGender()
            );
        }).collect(Collectors.toList());

        // Tạo Page từ danh sách DTO
        return new PageImpl<>(reportDtos, pageable, customerPage.getTotalElements());
    }

    /**
     * Lấy danh sách đơn hàng chi tiết của khách hàng, bao gồm SaleDetails.
     */
    public List<Sale> getCustomerSalesDetails(Integer customerId) {
        List<Sale> sales = saleRepository.findByCustomer_CustomerId(customerId);
        // Đảm bảo SaleDetails được tải (nếu dùng Lazy loading)
        sales.forEach(sale -> sale.getSaleDetails().size()); // Trigger loading
        return sales;
    }

    /**
     * Lấy thông tin khách hàng dựa trên customerId.
     */
    public CustomerReportDto getCustomerReportInfo(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        Long totalSales = saleService.countSalesByCustomerId(customerId);
        List<Sale> sales = saleRepository.findByCustomer_CustomerId(customerId);
        BigDecimal totalAmount = sales.stream()
                .map(Sale::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CustomerReportDto(
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getPhoneNumber(),
                customer.getEmail(),
                totalSales != null ? totalSales.intValue() : 0,
                totalAmount,
                calculateAge(customer.getBirthdayDate()),
                customer.getGender()
        );
    }

    /**
     * Tính tuổi dựa trên ngày sinh.
     */
    private Integer calculateAge(LocalDate birthdayDate) {
        if (birthdayDate == null) return null;
        LocalDate now = LocalDate.now();
        return Period.between(birthdayDate, now).getYears();
    }
}