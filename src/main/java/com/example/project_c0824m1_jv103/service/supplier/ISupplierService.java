package com.example.project_c0824m1_jv103.service.supplier;

import com.example.project_c0824m1_jv103.model.Supplier;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ISupplierService {
    Optional<Supplier> findById(Integer id);
    void saveSupplier(Supplier supplier, MultipartFile image);
    List<Supplier> findAll();
} 