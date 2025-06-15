package com.example.project_c0824m1_jv103.repository;

import com.example.project_c0824m1_jv103.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ISupplierRepository extends JpaRepository<Supplier, Integer> {
    @Override
    Page<Supplier> findAll(Pageable pageable);
    @Query("SELECT s FROM Supplier s WHERE " +
            "(:suplierName IS NULL OR s.suplierName LIKE %:suplierName%) AND " +
            "(:phoneNumber IS NULL OR s.phoneNumber LIKE %:phoneNumber%) AND " +
            "(:email IS NULL OR s.email LIKE %:email%)")
    Page<Supplier> findByCriteria(@Param("suplierName") String suplierName,
                                  @Param("phoneNumber") String phoneNumber,
                                  @Param("email") String email,
                                  Pageable pageable);
}
