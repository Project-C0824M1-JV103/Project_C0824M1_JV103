package com.example.project_c0824m1_jv103.service.sale;

import com.example.project_c0824m1_jv103.model.Sale;
import com.example.project_c0824m1_jv103.model.SaleDetails;
import com.example.project_c0824m1_jv103.model.Product;
import com.example.project_c0824m1_jv103.repository.ISaleRepository;
import com.example.project_c0824m1_jv103.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class SaleService implements ISaleService {

    @Autowired
    private ISaleRepository saleRepository;

    @Autowired
    private IProductRepository productRepository;

    @Override
    public Sale createSale(Sale sale) {
        // Cập nhật số lượng sản phẩm trong kho
        for (SaleDetails detail : sale.getSaleDetails()) {
            Product product = detail.getProduct();
            int newQuantity = product.getQuantity() - detail.getQuantity();
            if (newQuantity < 0) {
                throw new RuntimeException("Sản phẩm " + product.getProductName() + " không đủ số lượng trong kho");
            }
            product.setQuantity(newQuantity);
            productRepository.save(product);
        }
        
        // Lưu đơn hàng
        return saleRepository.save(sale);
    }

    @Override
    public Optional<Sale> findById(Integer id) {
        return saleRepository.findById(id);
    }

    @Override
    public Long countSalesByCustomerId(Integer customerId) {
        return saleRepository.countSalesByCustomerId(customerId);
    }

    @Override
    public Page<Sale> findAll(Pageable pageable) {
        return saleRepository.findAll(pageable);
    }

    @Override
    public int countSalesByDate(java.time.LocalDate date) {
        java.time.LocalDateTime start = date.atStartOfDay();
        java.time.LocalDateTime end = date.plusDays(1).atStartOfDay();
        return saleRepository.countSalesByDate(start, end);
    }

    @Override
    public java.math.BigDecimal sumAmountByDate(java.time.LocalDate date) {
        java.time.LocalDateTime start = date.atStartOfDay();
        java.time.LocalDateTime end = date.plusDays(1).atStartOfDay();
        java.math.BigDecimal sum = saleRepository.sumAmountByDate(start, end);
        return sum != null ? sum : java.math.BigDecimal.ZERO;
    }

    @Override
    public int countSalesByMonth(int year, int month) {
        return saleRepository.countSalesByMonth(year, month);
    }

    @Override
    public java.math.BigDecimal sumAmountByMonth(int year, int month) {
        java.math.BigDecimal sum = saleRepository.sumAmountByMonth(year, month);
        return sum != null ? sum : java.math.BigDecimal.ZERO;
    }
}