package com.example.project_c0824m1_jv103.service.supplier;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.project_c0824m1_jv103.model.Supplier;


import com.example.project_c0824m1_jv103.repository.ISupplierRepository;
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
    public Page<Supplier> findAll(Pageable pageable) {
        return supplierRepository.findAll(pageable);
    }
}

