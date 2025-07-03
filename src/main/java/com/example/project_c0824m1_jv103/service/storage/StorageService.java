package com.example.project_c0824m1_jv103.service.storage;

import com.example.project_c0824m1_jv103.dto.*;
import com.example.project_c0824m1_jv103.model.*;
import com.example.project_c0824m1_jv103.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

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
import java.util.stream.Collectors;
import java.util.ArrayList;
import com.example.project_c0824m1_jv103.repository.IProductImagesRepository;

@Service
public class StorageService implements IStorageService {
    @Autowired
    private IStorageTransactionRepository TRstorageRepository;

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

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private IProductImagesRepository productImagesRepository;

    @Override
    @Transactional
    public Storage exportProduct(StorageExportDTO exportDTO) {
        // Validation: Check export quantity is positive
        if (exportDTO.getExportQuantity() == null || exportDTO.getExportQuantity() <= 0) {
            throw new RuntimeException("Số lượng xuất phải lớn hơn 0");
        }

        // Find the specific storage record by storageId
        Storage storage = storageRepository.findById(exportDTO.getStorageId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lô hàng này trong kho."));

        // Check if there is enough quantity in this specific storage batch
        if (storage.getQuantity() < exportDTO.getExportQuantity()) {
            throw new RuntimeException("Số lượng trong lô hàng này không đủ để xuất. Chỉ còn " + storage.getQuantity() + " sản phẩm.");
        }

        // Get the product to update its retail quantity
        Product product = storage.getProduct();

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
                storage.getCost(), // Sử dụng giá nhập của lô cụ thể
                employee,
                StorageTransaction.TransactionType.EXPORT,
                "Xuất kho " + exportDTO.getExportQuantity() + " sản phẩm " + product.getProductName() + " từ lô " + storage.getStorageId()
            );
            
            storageTransactionRepository.save(exportTransaction);
        } catch (Exception e) {
            // Log the error but don't fail the main transaction
            System.err.println("Failed to create export transaction history: " + e.getMessage());
        }

        // Return the updated storage object
        return storage;
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
        if (importDTO.getProductId() == null) {
            throw new RuntimeException("Vui lòng chọn sản phẩm để nhập kho");
        }
        if (importDTO.getImportQuantity() == null || importDTO.getImportQuantity() < 1) {
            throw new RuntimeException("Số lượng nhập phải lớn hơn 0");
        }
        if (importDTO.getCost() == null || importDTO.getCost() < 0) {
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
        
        // Tìm Storage theo cả productId và cost để tránh lỗi unique result
        Optional<Storage> existingStorageOpt = storageRepository.findByProduct_ProductIdAndCost(importDTO.getProductId(), importDTO.getCost());
        Storage savedStorage;
        
        if (existingStorageOpt.isPresent()) {
            // Tìm thấy Storage với cùng productId và cost -> cộng dồn
            Storage existingStorage = existingStorageOpt.get();
            existingStorage.setQuantity(existingStorage.getQuantity() + importDTO.getImportQuantity());
            existingStorage.setEmployee(employee);
            existingStorage.setTransactionDate(java.time.LocalDateTime.now());
            savedStorage = storageRepository.save(existingStorage);
        } else {
            // Không tìm thấy Storage với cùng productId và cost -> tạo mới
            Storage newStorage = new Storage();
            newStorage.setProduct(product);
            newStorage.setQuantity(importDTO.getImportQuantity());
            newStorage.setCost(importDTO.getCost());
            newStorage.setEmployee(employee);
            newStorage.setTransactionDate(java.time.LocalDateTime.now());
            savedStorage = storageRepository.save(newStorage);
        }

        try {
            StorageTransaction importTransaction = new StorageTransaction(
                product,
                importDTO.getImportQuantity(),
                importDTO.getCost(),
                employee,
                StorageTransaction.TransactionType.IMPORT,
                "Nhập kho " + importDTO.getImportQuantity() + " sản phẩm " + product.getProductName()
            );
            storageTransactionRepository.save(importTransaction);
        } catch (Exception e) {
            System.err.println("Failed to create import transaction history: " + e.getMessage());
        }

        return savedStorage;
    }

    @Override
    public Page<Storage> getImportHistory(Pageable pageable) {
        return storageRepository.findAllImports(pageable);
    }

    @Override
    @Transactional
    public StorageTransaction updateStorageTransaction(Integer transactionId, StorageDto storageDto) {
        StorageTransaction existingTransaction = storageTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi giao dịch"));

        // Validate input
        if (storageDto.getQuantity() == null || storageDto.getQuantity() <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }

        if (storageDto.getCost() == null || storageDto.getCost() <= 0) {
            throw new RuntimeException("Giá nhập phải lớn hơn 0");
        }

        // Chỉ cho phép chỉnh sửa transaction IMPORT
        if (existingTransaction.getTransactionType() != StorageTransaction.TransactionType.IMPORT) {
            throw new RuntimeException("Chỉ có thể chỉnh sửa giao dịch nhập kho");
        }

        // Update fields that can be changed
        Integer oldQuantity = existingTransaction.getQuantity();
        Integer newQuantity = storageDto.getQuantity();

        // Update transaction record
        existingTransaction.setQuantity(newQuantity);
        existingTransaction.setCost(storageDto.getCost());
        existingTransaction.setTransactionDate(LocalDateTime.now());

        // Update employee if provided
        if (storageDto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(storageDto.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
            existingTransaction.setEmployee(employee);
        }

        // Update description
        existingTransaction.setDescription("Cập nhật giao dịch nhập kho " + newQuantity + " sản phẩm " + existingTransaction.getProduct().getProductName());

        // Update related storage records
        Product product = existingTransaction.getProduct();
        int quantityDifference = newQuantity - oldQuantity;
        
        // Cập nhật tồn kho trong Storage
        List<Storage> storages = storageRepository.findByProduct_ProductIdAndCostOrderByTransactionDateAsc(
                product.getProductId(), storageDto.getCost());
        
        if (!storages.isEmpty()) {
            // Cập nhật storage record có cùng cost
            Storage storageToUpdate = storages.get(0);
            storageToUpdate.setQuantity(storageToUpdate.getQuantity() + quantityDifference);
            storageToUpdate.setTransactionDate(LocalDateTime.now());
            storageRepository.save(storageToUpdate);
        }

        return storageTransactionRepository.save(existingTransaction);
    }

    @Override
    public StorageTransaction getStorageTransactionById(Integer transactionId) {
        return storageTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch"));
    }

    @Override
    @Transactional
    public void deleteStorageTransaction(Integer transactionId) {
        StorageTransaction transaction = storageTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch"));
        
        // Chỉ cho phép xóa giao dịch IMPORT
        if (transaction.getTransactionType() != StorageTransaction.TransactionType.IMPORT) {
            throw new RuntimeException("Chỉ có thể xóa giao dịch nhập kho");
        }
        
        // Cập nhật lại số lượng trong Storage
        Product product = transaction.getProduct();
        List<Storage> storages = storageRepository.findByProduct_ProductIdAndCostOrderByTransactionDateAsc(
                product.getProductId(), transaction.getCost());
        
        if (!storages.isEmpty()) {
            Storage storageToUpdate = storages.get(0);
            int newQuantity = storageToUpdate.getQuantity() - transaction.getQuantity();
            
            if (newQuantity < 0) {
                throw new RuntimeException("Không thể xóa giao dịch này vì sẽ làm số lượng tồn kho trở thành âm");
            }
            
            if (newQuantity == 0) {
                // Xóa storage record nếu số lượng = 0
                storageRepository.delete(storageToUpdate);
            } else {
                // Cập nhật số lượng mới
                storageToUpdate.setQuantity(newQuantity);
                storageRepository.save(storageToUpdate);
            }
        }
        
        // Xóa giao dịch
        storageTransactionRepository.delete(transaction);
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
        // Sắp xếp theo transactionDate giảm dần (mới nhất lên đầu)
        storages.sort(Comparator.comparing(Storage::getTransactionDate, Comparator.reverseOrder()));

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
        // Thay vì trả về Optional<Storage>, trả về Storage đầu tiên tìm được
        // hoặc null nếu không tìm thấy để tránh lỗi unique result
        List<Storage> storages = storageRepository.findAll().stream()
            .filter(storage -> storage.getProduct().getProductId().equals(productId))
            .collect(Collectors.toList());
        
        return storages.isEmpty() ? Optional.empty() : Optional.of(storages.get(0));
    }

    @Override
    public Page<Storage> searchProductsInStorage(String keyword, Pageable pageable) {
        return storageRepository.findByProduct_ProductNameContainingIgnoreCase(keyword, pageable);
    }
    @Override
    @Transactional
    public void saveStorage(StorageTransaction storage) {
        Product product = productRepository.findById(storage.getProduct().getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        Employee employee = storage.getEmployee() != null ? employeeRepository.findById(storage.getEmployee().getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại")) : null;

        storage.setProduct(product);
        storage.setEmployee(employee);
        if (storage.getTransactionType() == StorageTransaction.TransactionType.IMPORT) {
            product.setQuantity(product.getQuantity() + storage.getQuantity());
        } else if (storage.getTransactionType() == StorageTransaction.TransactionType.EXPORT) {
            product.setQuantity(product.getQuantity() - storage.getQuantity());
        }
        productRepository.save(product);

        TRstorageRepository.save(storage);
    }

    @Override
    public Page<StorageTransaction> findAll(Pageable pageable) {
        return TRstorageRepository.findAll(pageable);
    }

    @Override
    public Page<StorageTransaction> findByCriteria(String productName, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        List<StorageTransaction> storages = TRstorageRepository.findAll();

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

        // Lọc chỉ nhập kho (IMPORT)
        storages = storages.stream()
                .filter(storage -> storage.getTransactionType() == StorageTransaction.TransactionType.IMPORT)
                .collect(Collectors.toList());

        // Sắp xếp theo transactionDate giảm dần (mới nhất lên đầu)
        storages.sort(Comparator.comparing(StorageTransaction::getTransactionDate, Comparator.reverseOrder()));

        // Phân trang
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), storages.size());
        List<StorageTransaction> pagedStorages = storages.subList(start, end);

        return new PageImpl<>(pagedStorages, pageable, storages.size());
    }
}
