package com.example.project_c0824m1_jv103.repository;

import com.example.project_c0824m1_jv103.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findByProductNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findByPriceLessThanEqual(Double price, Pageable pageable);
    Page<Product> findByQuantityLessThanEqual(Integer quantity, Pageable pageable);
}
