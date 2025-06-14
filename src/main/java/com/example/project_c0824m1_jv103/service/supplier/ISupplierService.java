package com.example.project_c0824m1_jv103.service.supplier;

import com.example.project_c0824m1_jv103.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ISupplierService {
    Page<Supplier> findAll(Pageable pageable);
} 