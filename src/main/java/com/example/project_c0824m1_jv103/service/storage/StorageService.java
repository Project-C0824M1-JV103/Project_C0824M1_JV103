package com.example.project_c0824m1_jv103.service.storage;

import com.example.project_c0824m1_jv103.dto.StorageExportDTO;
import com.example.project_c0824m1_jv103.model.Product;
import com.example.project_c0824m1_jv103.model.Storage;
import com.example.project_c0824m1_jv103.repository.IProductRepository;
import com.example.project_c0824m1_jv103.repository.IStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

@Service
public class StorageService implements IStorageService {

    @Autowired
    private IStorageRepository storageRepository;

    @Autowired
    private IProductRepository productRepository;

    @Override
    @Transactional
    public Storage exportProduct(StorageExportDTO exportDTO) {
        // Validation: Check export quantity is positive
        if (exportDTO.getExportQuantity() == null || exportDTO.getExportQuantity() <= 0) {
            throw new RuntimeException("Số lượng xuất phải lớn hơn 0");
        }
        
        Product product = productRepository.findById(exportDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() < exportDTO.getExportQuantity()) {
            throw new RuntimeException("Not enough quantity in stock");
        }

        // Update product quantity
        product.setQuantity(product.getQuantity() - exportDTO.getExportQuantity());
        productRepository.save(product);

        // Create storage record for export
        Storage storage = new Storage();
        storage.setProduct(product);
        storage.setQuantity(-exportDTO.getExportQuantity()); // Negative quantity for export
        storage.setCost(product.getPrice()); // Using product price as cost

        return storageRepository.save(storage);
    }

    @Override
    public Page<Storage> getAllStorageRecords(Pageable pageable) {
        return storageRepository.findAll(pageable);
    }

    @Override
    public Storage getStorageById(Integer id) {
        return storageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Storage record not found"));
    }

    @Override
    public Page<Storage> getExportHistory(Pageable pageable) {
        Page<Storage> all = storageRepository.findAll(pageable);
        // Lọc các bản ghi xuất kho (quantity < 0)
        return new PageImpl<>(
            all.getContent().stream().filter(s -> s.getQuantity() < 0).collect(Collectors.toList()),
            pageable,
            all.getTotalElements()
        );
    }
} 