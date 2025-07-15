package com.example.project_c0824m1_jv103.service.supplier;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.project_c0824m1_jv103.dto.SupplierDto;
import com.example.project_c0824m1_jv103.mapper.SupplierMapper;
import com.example.project_c0824m1_jv103.model.Supplier;


import com.example.project_c0824m1_jv103.repository.ISupplierRepository;
import com.example.project_c0824m1_jv103.service.CloudinaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SupplierService implements ISupplierService {
    private static final Logger logger = LoggerFactory.getLogger(SupplierService.class);
    @Autowired
    private ISupplierRepository supplierRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private SupplierMapper supplierMapper;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public Optional<Supplier> findById(Integer id) {
        return supplierRepository.findById(id);
    }

    @Override
    public void saveSupplier(Supplier supplier, MultipartFile image) {
        logger.info("Lưu nhà cung cấp ID: {}, Tên: {}, Có ảnh mới: {}",
                supplier.getSuplierId(), supplier.getSuplierName(), image != null && !image.isEmpty());
        // Lấy nhà cung cấp hiện tại từ database để giữ imageUrl cũ
        if (supplier.getSuplierId() != null) {
            Optional<Supplier> existingSupplier = supplierRepository.findById(supplier.getSuplierId());
            if (existingSupplier.isPresent() && (image == null || image.isEmpty())) {
                supplier.setImageUrl(existingSupplier.get().getImageUrl());
            }
        }
        // Tải ảnh mới lên Cloudinary nếu có
        if (image != null && !image.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(),
                        ObjectUtils.asMap("folder", "suppliers"));
                supplier.setImageUrl(uploadResult.get("url").toString());
            } catch (IOException e) {
                logger.error("Lỗi khi tải ảnh lên Cloudinary: {}", e.getMessage());
                throw new RuntimeException("Lỗi khi tải ảnh lên Cloudinary: " + e.getMessage());
            }
        }
        // Lưu nhà cung cấp
        supplierRepository.save(supplier);
        logger.info("Đã lưu nhà cung cấp thành công: {}", supplier.getSuplierName());
    }

    @Override
    public Page<Supplier> findByCriteria(String suplierName, String phoneNumber, String email, Pageable pageable) {
        return supplierRepository.findByCriteria(suplierName,phoneNumber, email, pageable);
    }

    @Override
    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier saveSupplier(SupplierDto supplierDto) throws IOException {
        // Validate trước khi lưu
        String validationError = validateNewSupplier(supplierDto);
        if (validationError != null) {
            throw new RuntimeException(validationError);
        }

        // Tạo supplier từ DTO bằng mapper
        Supplier supplier = supplierMapper.toEntity(supplierDto);

        // Upload ảnh nếu có
        if (supplierDto.getImageFile() != null && !supplierDto.getImageFile().isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(supplierDto.getImageFile());
                supplier.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new IOException("Lỗi khi tải ảnh lên: " + e.getMessage());
            }
        }

        // Lưu supplier vào database
        Supplier savedSupplier = supplierRepository.save(supplier);

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
    public boolean isSupplierNameExists(String suplierName) {
        if (suplierName == null || suplierName.trim().isEmpty()) {
            return false;
        }
        return supplierRepository.existsBySupplierNameIgnoreCase(suplierName.trim());
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

//    @Override
//    public String validateNewSupplier(SupplierDto supplierDto) {
//        if (supplierDto == null) {
//            return "Thông tin nhà cung cấp không được để trống";
//        }
//
//        // Kiểm tra tên nhà cung cấp đã tồn tại
//        if (supplierDto.getSuplierName() != null && !supplierDto.getSuplierName().trim().isEmpty()) {
//            if (isSupplierNameExists(supplierDto.getSuplierName())) {
//                return "Tên nhà cung cấp '" + supplierDto.getSuplierName() + "' đã được sử dụng bởi nhà cung cấp khác";
//            }
//        }
//
//        // Kiểm tra email đã tồn tại
//        if (supplierDto.getEmail() != null && !supplierDto.getEmail().trim().isEmpty()) {
//            if (isEmailExists(supplierDto.getEmail())) {
//                return "Email '" + supplierDto.getEmail() + "' đã được sử dụng bởi nhà cung cấp khác";
//            }
//        }
//
//        // Kiểm tra số điện thoại đã tồn tại
//        if (supplierDto.getPhoneNumber() != null && !supplierDto.getPhoneNumber().trim().isEmpty()) {
//            if (isPhoneNumberExists(supplierDto.getPhoneNumber())) {
//                return "Số điện thoại '" + supplierDto.getPhoneNumber() + "' đã được sử dụng bởi nhà cung cấp khác";
//            }
//        }
//
//        return null; // Không có lỗi
//    }
@Override
public boolean isSupplierNameExistsForUpdate(String suplierName, Integer id) {
    if (suplierName == null || suplierName.trim().isEmpty() || id == null) return false;
    return supplierRepository.existsBySuplierNameIgnoreCaseAndSuplierIdNot(suplierName.trim(), id);
}

    @Override
public String validateNewSupplier(SupplierDto supplierDto) {
    if (supplierDto == null) {
        return "Thông tin nhà cung cấp không được để trống";
    }

    Integer id = supplierDto.getSuplierId();

    // Kiểm tra tên nhà cung cấp đã tồn tại
    if (supplierDto.getSuplierName() != null && !supplierDto.getSuplierName().trim().isEmpty()) {
        if (id == null) { // Thêm mới
            if (isSupplierNameExists(supplierDto.getSuplierName())) {
                return "Tên nhà cung cấp '" + supplierDto.getSuplierName() + "' đã được sử dụng bởi nhà cung cấp khác";
            }
        } else { // Chỉnh sửa
            if (isSupplierNameExistsForUpdate(supplierDto.getSuplierName(), id)) {
                return "Tên nhà cung cấp '" + supplierDto.getSuplierName() + "' đã được sử dụng bởi nhà cung cấp khác";
            }
        }
    }

    // Kiểm tra email đã tồn tại
    if (supplierDto.getEmail() != null && !supplierDto.getEmail().trim().isEmpty()) {
        if (id == null) { // Thêm mới
            if (isEmailExists(supplierDto.getEmail())) {
                return "Email đã được sử dụng bởi nhà cung cấp khác";
            }
        } else { // Chỉnh sửa
            if (isEmailExistsForUpdate(supplierDto.getEmail(), id)) {
                return "Email đã được sử dụng bởi nhà cung cấp khác";
            }
        }
    }

    // Kiểm tra số điện thoại đã tồn tại
    if (supplierDto.getPhoneNumber() != null && !supplierDto.getPhoneNumber().trim().isEmpty()) {
        if (id == null) { // Thêm mới
            if (isPhoneNumberExists(supplierDto.getPhoneNumber())) {
                return "Số điện thoại đã được sử dụng bởi nhà cung cấp khác";
            }
        } else { // Chỉnh sửa
            if (isPhoneNumberExistsForUpdate(supplierDto.getPhoneNumber(), id)) {
                return "Số điện thoại đã được sử dụng bởi nhà cung cấp khác";
            }
        }
    }

    return null; // Không có lỗi
}


    @Override
    public Page<Supplier> findAll(Pageable pageable) {
        return supplierRepository.findAll(pageable);
    }

    @Override
    public boolean isEmailExistsInSystem(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        Long count = supplierRepository.countEmailExistsInSystem(email);
        return count != null && count > 0;
    }

    @Override
    public boolean isPhoneExistsInSystem(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        Long count = supplierRepository.countPhoneExistsInSystem(phone);
        return count != null && count > 0;
    }

    @Override
    public boolean isSupplierNameExistsInSystem(String supplierName) {
        if (supplierName == null || supplierName.trim().isEmpty()) {
            return false;
        }
        Long count = supplierRepository.countSupplierNameExistsInSystem(supplierName);
        return count != null && count > 0;
    }
}

