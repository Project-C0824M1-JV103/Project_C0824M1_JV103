package com.example.project_c0824m1_jv103.service.storage;

import com.example.project_c0824m1_jv103.dto.*;
import com.example.project_c0824m1_jv103.model.*;
import com.example.project_c0824m1_jv103.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import com.example.project_c0824m1_jv103.dto.StorageDto;
import com.example.project_c0824m1_jv103.dto.StorageImportDTO;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.model.Product;

import com.example.project_c0824m1_jv103.dto.StorageImportDTO;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.model.Product;
import com.example.project_c0824m1_jv103.model.Storage;
import com.example.project_c0824m1_jv103.repository.IEmployeeRepository;
import com.example.project_c0824m1_jv103.repository.IProductRepository;
import com.example.project_c0824m1_jv103.model.Supplier;
import com.example.project_c0824m1_jv103.repository.IEmployeeRepository;
import com.example.project_c0824m1_jv103.repository.IProductRepository;
import com.example.project_c0824m1_jv103.repository.IStorageRepository;
import com.example.project_c0824m1_jv103.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.project_c0824m1_jv103.repository.ISupplierRepository;
import com.example.project_c0824m1_jv103.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private ISupplierRepository supplierRepository;

    @Autowired
    private IStorageTransactionRepository storageTransactionRepository;

    @Override
    @Transactional
    public Storage exportProduct(StorageExportDTO exportDTO) {
        // Validation: Check export quantity is positive
        if (exportDTO.getExportQuantity() == null || exportDTO.getExportQuantity() <= 0) {
            throw new RuntimeException("Số lượng xuất phải lớn hơn 0");
        }

        // Find the storage record for the product, which holds the actual warehouse stock
        Storage storage = storageRepository.findByProduct_ProductId(exportDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho chứa sản phẩm này."));

        // Check if there is enough quantity in storage (warehouse)
        if (storage.getQuantity() < exportDTO.getExportQuantity()) {
            throw new RuntimeException("Số lượng trong kho không đủ để xuất.");
        }

        // Find the product to update its retail quantity
        Product product = productRepository.findById(exportDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        // Decrease quantity in storage
        storage.setQuantity(storage.getQuantity() - exportDTO.getExportQuantity());
        storageRepository.save(storage);

        // Increase quantity in product (retail)
        product.setQuantity(product.getQuantity() + exportDTO.getExportQuantity());
        productRepository.save(product);

        // Create transaction history for export
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Employee employee = employeeRepository.findByEmail(email);
            
            StorageTransaction exportTransaction = new StorageTransaction(
                product,
                -exportDTO.getExportQuantity(), // Số âm cho xuất kho
                product.getPrice(),
                employee,
                StorageTransaction.TransactionType.EXPORT,
                "Xuất kho " + exportDTO.getExportQuantity() + " sản phẩm " + product.getProductName()
            );
            
            storageTransactionRepository.save(exportTransaction);
        } catch (Exception e) {
            // Log the error but don't fail the main transaction
            System.err.println("Failed to create export transaction history: " + e.getMessage());
        }

        // Return the updated storage object
        return storage;
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
        // Sử dụng StorageTransaction để lấy lịch sử xuất kho
        Page<StorageTransaction> exportTransactions = storageTransactionRepository.findExportHistory(pageable);
        
        // Chuyển đổi StorageTransaction thành Storage để tương thích với interface hiện tại
        List<Storage> storageList = exportTransactions.getContent().stream()
            .map(transaction -> {
                Storage storage = new Storage();
                storage.setStorageId(transaction.getTransactionId());
                storage.setProduct(transaction.getProduct());
                storage.setQuantity(transaction.getQuantity()); // Số âm cho xuất kho
                storage.setCost(transaction.getCost());
                storage.setEmployee(transaction.getEmployee());
                storage.setTransactionDate(transaction.getTransactionDate());
                return storage;
            })
            .collect(Collectors.toList());
        
        return new PageImpl<>(storageList, pageable, exportTransactions.getTotalElements());
    }

    @Override
    public List<Storage> findAllStorage() {
        return storageRepository.findAll();
    }

    @Override
    @Transactional
    public Storage importProduct(StorageImportDTO importDTO) {
        if (importDTO.getImportQuantity() == null || importDTO.getImportQuantity() <= 0) {
            throw new RuntimeException("Số lượng nhập phải lớn hơn 0");
        }

        if (importDTO.getCost() != null && importDTO.getCost() < 0) {
            throw new RuntimeException("Giá nhập không hợp lệ");
        }

        Product product = productRepository.findById(importDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        if (!product.getSupplier().getSuplierId().equals(importDTO.getSupplierId())) {
            throw new RuntimeException("Nhà cung cấp không đúng. Hãy thêm sản phẩm mới.");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null) {
            throw new RuntimeException("Nhân viên không tồn tại");
        }

        Optional<Storage> optionalStorage = storageRepository.findByProduct_ProductId(product.getProductId());

        if (optionalStorage.isPresent()) {
            Storage existingStorage = optionalStorage.get();
            existingStorage.setQuantity(existingStorage.getQuantity() + importDTO.getImportQuantity());

            if (importDTO.getCost() != null) {
                existingStorage.setCost(importDTO.getCost());
            }

            existingStorage.setEmployee(employee);
            return storageRepository.save(existingStorage);
        }

        Storage newStorage = new Storage();
        newStorage.setProduct(product);
        newStorage.setQuantity(importDTO.getImportQuantity());
        newStorage.setCost(importDTO.getCost() != null ? importDTO.getCost() : product.getPrice());
        newStorage.setEmployee(employee);
        newStorage.setTransactionDate(LocalDateTime.now());
        return storageRepository.save(newStorage);
    }

    @Override
    public void importProduct(StorageImportId storageImportId) {
        Storage storage = new Storage();
        Product product = productRepository.findById(storageImportId.getProductId()).orElse(null);
        if (product == null) throw new RuntimeException("Product not found");
        storage.setProduct(product);
        storage.setCost(0.0);
        storage.setQuantity(0);
        if (storageImportId.getEmployeeId() == null) throw new RuntimeException("EmployeeId is required");
        Employee employee = employeeRepository.findById(storageImportId.getEmployeeId())
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        storage.setEmployee(employee);
        storageRepository.save(storage);
    }

    @Override
    public Page<Storage> getImportHistory(Pageable pageable) {
        return storageRepository.findAllImports(pageable);
    }


    @Override
    @Transactional
    public Storage updateStorage(Integer storageId, StorageDto storageDto) {
        Storage existingStorage = storageRepository.findById(storageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi nhập kho"));

        // Validate input
        if (storageDto.getQuantity() == null || storageDto.getQuantity() <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }

        if (storageDto.getCost() == null || storageDto.getCost() <= 0) {
            throw new RuntimeException("Giá nhập phải lớn hơn 0");
        }

        // Update fields that can be changed
        Integer oldQuantity = existingStorage.getQuantity();
        Integer newQuantity = storageDto.getQuantity();

        // Update storage record
        existingStorage.setQuantity(newQuantity);
        existingStorage.setCost(storageDto.getCost());

        // Update employee if provided
        if (storageDto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(storageDto.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
            existingStorage.setEmployee(employee);
        }

        // Update product quantity based on difference
        Product product = existingStorage.getProduct();
        int quantityDifference = newQuantity - oldQuantity;
        product.setQuantity(product.getQuantity() + quantityDifference);
        productRepository.save(product);

        return storageRepository.save(existingStorage);
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
                    .filter(storage -> storage.getProduct() != null &&
                            storage.getProduct().getProductName().toLowerCase().contains(productName.toLowerCase()))
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
            dto.setProductId(storage.getProduct() != null ? storage.getProduct().getProductId() : null);
            dto.setProductName(storage.getProduct() != null ? storage.getProduct().getProductName() : "N/A");
            dto.setQuantity(storage.getQuantity());
            dto.setCost(storage.getCost());
            dto.setEmployeeId(storage.getEmployee() != null ? storage.getEmployee().getEmployeeId() : null);
            dto.setEmployeeName(storage.getEmployee() != null ? storage.getEmployee().getFullName() : "Chưa xác định");
            dto.setTransactionDate(storage.getTransactionDate());
            return dto;
        }).collect(Collectors.toList());
    }

    public Page<StorageDto> paginateStorageList(List<StorageDto> storages, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), storages.size());
        return new PageImpl<>(storages.subList(start, end), pageable, storages.size());
    }

    @Override
    public Optional<Storage> findByProductId(Integer productId) {
        return storageRepository.findByProduct_ProductId(productId);
    }

    @Override
    public Page<Storage> searchProductsInStorage(String keyword, Pageable pageable) {
        return storageRepository.findByProduct_ProductNameContainingIgnoreCase(keyword, pageable);
    }
}
