package com.example.project_c0824m1_jv103.service.storage;

import com.example.project_c0824m1_jv103.dto.StorageDto;
import com.example.project_c0824m1_jv103.dto.StorageExportDTO;
import com.example.project_c0824m1_jv103.dto.StorageImportDTO;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.model.Product;

import com.example.project_c0824m1_jv103.model.Storage;
import com.example.project_c0824m1_jv103.repository.IEmployeeRepository;
import com.example.project_c0824m1_jv103.repository.IProductRepository;
import com.example.project_c0824m1_jv103.repository.IStorageRepository;
import com.example.project_c0824m1_jv103.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageService implements IStorageService {

    @Autowired
    private IStorageRepository storageRepository;

    @Autowired
    private IProductRepository productRepository;
    @Autowired
    private IEmployeeRepository employeeRepository;
    @Autowired
    private CloudinaryService cloudinaryService;


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
//    @Override
//    @Transactional
//    public Storage importProduct(StorageImportDTO importDTO) {
//        Product product = productRepository.findById(importDTO.getProductId())
//                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
//
//        Employee employee = employeeRepository.findById(importDTO.getEmployeeId())
//                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));
//
//        product.setQuantity(product.getQuantity() + importDTO.getImportQuantity());
//        productRepository.save(product);
//
//        Storage storage = new Storage();
//        storage.setProduct(product);
//        storage.setQuantity(importDTO.getImportQuantity());
//        storage.setCost(importDTO.getCost());
//        storage.setEmployee(employee);
//
//        return storageRepository.save(storage);
//    }

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
    @Override
    public Page<Storage> getImportHistory(Pageable pageable) {
        return storageRepository.findAllImports(pageable);

    }

    @Override
    public List<StorageDto> findAll() {
        return storageRepository.findAll().stream().map(storage -> {
            StorageDto dto = new StorageDto();
            dto.setStorageId(storage.getStorageId());
            dto.setProductId(storage.getProduct().getProductId());
            dto.setProductName(storage.getProduct().getProductName());
            dto.setQuantity(storage.getQuantity());
            dto.setCost(storage.getCost());
            dto.setEmployeeId(storage.getEmployee() != null ? storage.getEmployee().getEmployeeId() : null);
            dto.setEmployeeName(storage.getEmployee() != null ? storage.getEmployee().getFullName() : "Chưa xác định");
            dto.setTransactionDate(storage.getTransactionDate());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<StorageDto> findByCriteria(String productName, LocalDate startDate, LocalDate endDate) {
        List<Storage> storages = storageRepository.findAll();

        // Áp dụng điều kiện lọc linh hoạt
        if (productName != null && !productName.isEmpty()) {
            storages = storages.stream()
                    .filter(storage -> storage.getProduct().getProductName().toLowerCase().contains(productName.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (startDate != null || endDate != null) {
            storages = storages.stream()
                    .filter(storage -> {
                        LocalDate transactionDate = storage.getTransactionDate().toLocalDate();
                        boolean isAfterStart = startDate == null || !transactionDate.isBefore(startDate);
                        boolean isBeforeEnd = endDate == null || !transactionDate.isAfter(endDate);
                        return isAfterStart && isBeforeEnd;
                    })
                    .collect(Collectors.toList());
        }

        return storages.stream().map(storage -> {
            StorageDto dto = new StorageDto();
            dto.setStorageId(storage.getStorageId());
            dto.setProductId(storage.getProduct().getProductId());
            dto.setProductName(storage.getProduct().getProductName());
            dto.setQuantity(storage.getQuantity());
            dto.setCost(storage.getCost());
            dto.setEmployeeId(storage.getEmployee() != null ? storage.getEmployee().getEmployeeId() : null);
            dto.setEmployeeName(storage.getEmployee() != null ? storage.getEmployee().getFullName() : "Chưa xác định");
            dto.setTransactionDate(storage.getTransactionDate());
            return dto;
        }).collect(Collectors.toList());
    }
    @Override
    public Page<StorageDto> paginateStorageList(List<StorageDto> storageList, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), storageList.size());

        return new PageImpl<>(
                storageList.subList(start, end),
                pageable,
                storageList.size()
        );
    }
} 