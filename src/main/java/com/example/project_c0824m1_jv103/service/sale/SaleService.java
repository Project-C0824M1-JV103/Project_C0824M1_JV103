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
    public Page<Sale> findAll(Pageable pageable) {
        return saleRepository.findAll(pageable);
    }
}