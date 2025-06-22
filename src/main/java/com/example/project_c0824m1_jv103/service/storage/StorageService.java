package com.example.project_c0824m1_jv103.service.storage;

import com.example.project_c0824m1_jv103.dto.StorageImportDTO;
import com.example.project_c0824m1_jv103.model.*;
import com.example.project_c0824m1_jv103.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import com.example.project_c0824m1_jv103.dto.StorageDto;
import com.example.project_c0824m1_jv103.dto.StorageDto;
import com.example.project_c0824m1_jv103.dto.StorageExportDTO;
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
    public List<Storage> findAllStorage() {
        return storageRepository.findAll();
    }

    @Override
    @Transactional
    public Storage importProduct(StorageImportDTO importDTO) {
        if (importDTO.getImportQuantity() == null || importDTO.getImportQuantity() <= 0) {
            throw new RuntimeException("Số lượng nhập phải lớn hơn 0");
        }

        if (importDTO.getCost() != null && importDTO.getCost() <= 0) {
            throw new RuntimeException("Giá nhập phải lớn hơn 0");
        }

        Product existingProduct = productRepository.findById(importDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        Supplier selectedSupplier = supplierRepository.findById(importDTO.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Nhà cung cấp không tồn tại"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null) {
            throw new RuntimeException("Nhân viên không tồn tại");
        }

        Product productToUse;

        if (!existingProduct.getSupplier().getSuplierId().equals(selectedSupplier.getSuplierId())) {
            productToUse = new Product(
                    existingProduct.getProductName(),
                    existingProduct.getSize(),
                    existingProduct.getPrice(),
                    existingProduct.getCameraFront(),
                    existingProduct.getCameraBack(),
                    existingProduct.getMemory(),
                    existingProduct.getCpu(),
                    existingProduct.getDescription(),
                    0,
                    existingProduct.getCategory(),
                    selectedSupplier
            );

            List<ProductImages> clonedImages = existingProduct.getProductImages().stream()
                    .map(img -> new ProductImages(productToUse, img.getImageUrl(), img.getCaption()))
                    .toList();

            productToUse.setProductImages(clonedImages);
            productRepository.save(productToUse);
        } else {
            productToUse = existingProduct;
        }

        productToUse.setQuantity(productToUse.getQuantity() + importDTO.getImportQuantity());
        productRepository.save(productToUse);

        Storage storage = new Storage();
        storage.setProduct(productToUse);
        storage.setQuantity(importDTO.getImportQuantity());
        storage.setCost(importDTO.getCost() != null ? importDTO.getCost() : productToUse.getPrice());
        storage.setEmployee(employee);

        return storageRepository.save(storage);
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