package com.example.project_c0824m1_jv103.service.storage;

import com.example.project_c0824m1_jv103.dto.StorageExportDTO;
import com.example.project_c0824m1_jv103.dto.StorageImportDTO;
import com.example.project_c0824m1_jv103.model.Storage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IStorageService {
    Storage exportProduct(StorageExportDTO exportDTO);
    Storage importProduct(StorageImportDTO importDTO);
    Page<Storage> getAllStorageRecords(Pageable pageable);
    Storage getStorageById(Integer id);
    Page<Storage> getExportHistory(Pageable pageable);
    Page<Storage> getImportHistory(Pageable pageable);
} 