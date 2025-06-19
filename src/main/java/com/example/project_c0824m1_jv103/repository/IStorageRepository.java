package com.example.project_c0824m1_jv103.repository;

import com.example.project_c0824m1_jv103.model.Storage;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;


public interface IStorageRepository extends JpaRepository<Storage, Integer> {
    @Query("SELECT s FROM Storage s WHERE s.quantity > 0")
    Page<Storage> findAllImports(Pageable pageable);
}
