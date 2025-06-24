package com.example.project_c0824m1_jv103.repository;

import com.example.project_c0824m1_jv103.model.StorageTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IStorageTransactionRepository extends JpaRepository<StorageTransaction, Integer> {
    
    // Lấy lịch sử xuất kho (EXPORT)
    Page<StorageTransaction> findByTransactionTypeOrderByTransactionDateDesc(
        StorageTransaction.TransactionType transactionType, Pageable pageable);
    
    // Lấy lịch sử nhập kho (IMPORT)
    @Query("SELECT st FROM StorageTransaction st WHERE st.transactionType = 'IMPORT' ORDER BY st.transactionDate DESC")
    Page<StorageTransaction> findImportHistory(Pageable pageable);
    
    // Lấy lịch sử xuất kho (EXPORT) 
    @Query("SELECT st FROM StorageTransaction st WHERE st.transactionType = 'EXPORT' ORDER BY st.transactionDate DESC")
    Page<StorageTransaction> findExportHistory(Pageable pageable);
} 