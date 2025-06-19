package com.example.project_c0824m1_jv103.service.storage;

import com.example.project_c0824m1_jv103.model.Storage;

import java.util.List;

import com.example.project_c0824m1_jv103.dto.StorageExportDTO;
import com.example.project_c0824m1_jv103.model.Storage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IStorageService {
    List<Storage> findAll();
    Storage exportProduct(StorageExportDTO exportDTO);
    Page<Storage> getAllStorageRecords(Pageable pageable);
    Storage getStorageById(Integer id);
    Page<Storage> getExportHistory(Pageable pageable);
}