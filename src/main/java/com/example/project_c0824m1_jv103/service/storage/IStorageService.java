package com.example.project_c0824m1_jv103.service.storage;

import com.example.project_c0824m1_jv103.dto.StorageDto;
import com.example.project_c0824m1_jv103.dto.StorageExportDTO;
import com.example.project_c0824m1_jv103.dto.StorageImportDTO;
import com.example.project_c0824m1_jv103.model.Storage;

import java.util.List;
import java.util.Optional;

import com.example.project_c0824m1_jv103.dto.StorageExportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IStorageService {
    List<Storage> findAllStorage();
    Storage exportProduct(StorageExportDTO exportDTO);
    Storage importProduct(StorageImportDTO importDTO);
    Page<Storage> getAllStorageRecords(Pageable pageable);
    Storage getStorageById(Integer id);
    Page<Storage> getExportHistory(Pageable pageable);
    Page<Storage> getImportHistory(Pageable pageable);

    List<StorageDto> findAll();
    List<StorageDto> findByCriteria(String productName, LocalDate startDate, LocalDate endDate);

    // Thêm method để chỉnh sửa nhập kho
    Storage updateStorage(Integer storageId, StorageDto storageDto);
    Page<StorageDto> paginateStorageList(List<StorageDto> storageList, Pageable pageable);

    void importProduct(com.example.project_c0824m1_jv103.dto.StorageImportId storageImportId);

    Optional<Storage> findByProductId(Integer productId);
}