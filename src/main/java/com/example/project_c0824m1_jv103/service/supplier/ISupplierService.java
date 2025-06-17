package com.example.project_c0824m1_jv103.service.supplier;

import com.example.project_c0824m1_jv103.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ISupplierService {
    Page<Supplier> findAll(Pageable pageable);
    Optional<Supplier> findById(Integer id);
    void saveSupplier(Supplier supplier, MultipartFile image);
    Page<Supplier> findByCriteria(String suplierName, String phoneNumber, String email, Pageable pageable);
}