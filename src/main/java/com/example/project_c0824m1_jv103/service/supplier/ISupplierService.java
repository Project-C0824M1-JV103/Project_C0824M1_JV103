package com.example.project_c0824m1_jv103.service.supplier;

import com.example.project_c0824m1_jv103.dto.SupplierDto;
import com.example.project_c0824m1_jv103.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ISupplierService {
    Page<Supplier> findAll(Pageable pageable);
    Optional<Supplier> findById(Integer id);
    void saveSupplier(Supplier supplier, MultipartFile image);
    Page<Supplier> findByCriteria(String suplierName, String phoneNumber, String email, Pageable pageable);

    Supplier saveSupplier(SupplierDto supplierDto) throws IOException;

    // Validation methods
    boolean isEmailExists(String email);
    boolean isPhoneNumberExists(String phoneNumber);
    boolean isEmailExistsForUpdate(String email, Integer id);
    boolean isPhoneNumberExistsForUpdate(String phoneNumber, Integer id);

    // Validation method for new supplier
    String validateNewSupplier(SupplierDto supplierDto);
}