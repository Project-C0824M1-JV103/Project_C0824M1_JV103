package com.example.project_c0824m1_jv103.service.supplier;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.project_c0824m1_jv103.model.Supplier;
import com.example.project_c0824m1_jv103.repository.ISupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
public class SupplierService implements ISupplierService {
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
        // Tải ảnh lên Cloudinary nếu có
        if (image != null && !image.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = uploadResult.get("url").toString();
                supplier.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi tải ảnh lên Cloudinary: " + e.getMessage());
            }
        }
        // Lưu nhà cung cấp
        supplierRepository.save(supplier);

    }
    @Override
    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }
} 