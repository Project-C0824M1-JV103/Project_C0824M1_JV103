package com.example.project_c0824m1_jv103.service.storage;

import com.example.project_c0824m1_jv103.model.Storage;
import com.example.project_c0824m1_jv103.repository.IStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorageService implements IStorageService {
    @Autowired
    private IStorageRepository storageRepository;

    @Override
    public List<Storage> findAll() {
        return storageRepository.findAll();
    }
}