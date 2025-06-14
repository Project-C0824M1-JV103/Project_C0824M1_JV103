package com.example.project_c0824m1_jv103.repository;

import com.example.project_c0824m1_jv103.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISupplierRepository extends JpaRepository<Supplier, Integer> {
    @Override
    Page<Supplier> findAll(Pageable pageable);
}
