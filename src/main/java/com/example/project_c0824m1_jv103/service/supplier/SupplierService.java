package com.example.project_c0824m1_jv103.service.supplier;

import com.example.project_c0824m1_jv103.dto.SupplierDto;
import com.example.project_c0824m1_jv103.mapper.SupplierMapper;
import com.example.project_c0824m1_jv103.model.Supplier;
import com.example.project_c0824m1_jv103.repository.ISupplierRepository;
import com.example.project_c0824m1_jv103.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class SupplierService implements ISupplierService {

    @Autowired
    private ISupplierRepository supplierRepository;
    
    @Autowired
    private CloudinaryService cloudinaryService;
    
    @Autowired
    private SupplierMapper supplierMapper;

    @Override
    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }
    
    @Override
    public Supplier saveSupplier(SupplierDto supplierDto) throws IOException {
        System.out.println("=== SUPPLIER CREATION START ===");
        System.out.println("Supplier name: " + supplierDto.getSuplierName());
        System.out.println("Email: " + supplierDto.getEmail());
        System.out.println("Phone: " + supplierDto.getPhoneNumber());
        
        // Validate trước khi lưu
        String validationError = validateNewSupplier(supplierDto);
        if (validationError != null) {
            System.err.println("Validation failed: " + validationError);
            throw new RuntimeException(validationError);
        }
        System.out.println("Validation passed");
        
        // Tạo supplier từ DTO bằng mapper
        Supplier supplier = supplierMapper.toEntity(supplierDto);
        
        // Upload ảnh nếu có
        if (supplierDto.getImageFile() != null && !supplierDto.getImageFile().isEmpty()) {
            System.out.println("Image file detected - Starting upload process...");
            try {
                String imageUrl = cloudinaryService.uploadImage(supplierDto.getImageFile());
                supplier.setImageUrl(imageUrl);
                System.out.println("Image uploaded successfully and linked to supplier");
            } catch (IOException e) {
                System.err.println("Image upload failed: " + e.getMessage());
                throw new IOException("Lỗi khi tải ảnh lên: " + e.getMessage());
            }
        } else {
            System.out.println("No image file provided - Skipping upload");
        }
        
        // Lưu supplier vào database
        System.out.println("Saving supplier to database...");
        Supplier savedSupplier = supplierRepository.save(supplier);
        
        System.out.println("SUPPLIER CREATED SUCCESSFULLY!");
        System.out.println("Supplier ID: " + savedSupplier.getSuplierId());
        System.out.println("Image URL: " + (savedSupplier.getImageUrl() != null ? savedSupplier.getImageUrl() : "No image"));
        System.out.println("Created at: " + new java.util.Date());
        System.out.println("=== SUPPLIER CREATION END ===\n");
        
        return savedSupplier;
    }
    
    @Override
    public boolean isEmailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return supplierRepository.existsByEmailIgnoreCase(email.trim());
    }
    
    @Override
    public boolean isPhoneNumberExists(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return supplierRepository.existsByPhoneNumber(phoneNumber.trim());
    }
    
    @Override
    public boolean isEmailExistsForUpdate(String email, Integer id) {
        if (email == null || email.trim().isEmpty() || id == null) {
            return false;
        }
        return supplierRepository.existsByEmailIgnoreCaseAndIdNot(email.trim(), id);
    }
    
    @Override
    public boolean isPhoneNumberExistsForUpdate(String phoneNumber, Integer id) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty() || id == null) {
            return false;
        }
        return supplierRepository.existsByPhoneNumberAndIdNot(phoneNumber.trim(), id);
    }
    
    @Override
    public String validateNewSupplier(SupplierDto supplierDto) {
        if (supplierDto == null) {
            return "Thông tin nhà cung cấp không được để trống";
        }
        
        // Kiểm tra email đã tồn tại
        if (supplierDto.getEmail() != null && !supplierDto.getEmail().trim().isEmpty()) {
            if (isEmailExists(supplierDto.getEmail())) {
                return "Email '" + supplierDto.getEmail() + "' đã được sử dụng bởi nhà cung cấp khác";
            }
        }
        
        // Kiểm tra số điện thoại đã tồn tại
        if (supplierDto.getPhoneNumber() != null && !supplierDto.getPhoneNumber().trim().isEmpty()) {
            if (isPhoneNumberExists(supplierDto.getPhoneNumber())) {
                return "Số điện thoại '" + supplierDto.getPhoneNumber() + "' đã được sử dụng bởi nhà cung cấp khác";
            }
        }
        
        return null; // Không có lỗi
    }
}