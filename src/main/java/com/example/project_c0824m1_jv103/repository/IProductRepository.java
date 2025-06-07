package com.example.project_c0824m1_jv103.repository;

import com.example.project_c0824m1_jv103.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductRepository extends JpaRepository<Product, Integer> {
}
