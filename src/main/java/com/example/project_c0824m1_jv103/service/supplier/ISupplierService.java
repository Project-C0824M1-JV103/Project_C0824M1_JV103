package com.example.project_c0824m1_jv103.service.supplier;

import com.example.project_c0824m1_jv103.dto.SupplierDto;
import com.example.project_c0824m1_jv103.model.Supplier;

import java.io.IOException;
import java.util.List;

public interface ISupplierService {
    List<Supplier> findAll();
    Supplier saveSupplier(SupplierDto supplierDto) throws IOException;
    
    // Validation methods
    boolean isEmailExists(String email);
    boolean isPhoneNumberExists(String phoneNumber);
    boolean isEmailExistsForUpdate(String email, Integer id);
    boolean isPhoneNumberExistsForUpdate(String phoneNumber, Integer id);
    
    // Validation method for new supplier
    String validateNewSupplier(SupplierDto supplierDto);
} 