package com.example.project_c0824m1_jv103.repository;

import com.example.project_c0824m1_jv103.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

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

    // Tìm nhà cung cấp theo email (không phân biệt chữ hoa thường)
    @Query("SELECT s FROM Supplier s WHERE LOWER(s.email) = LOWER(:email)")
    Optional<Supplier> findByEmailIgnoreCase(@Param("email") String email);

    // Tìm nhà cung cấp theo số điện thoại
    @Query("SELECT s FROM Supplier s WHERE s.phoneNumber = :phoneNumber")
    Optional<Supplier> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    // Kiểm tra tồn tại theo email (không phân biệt chữ hoa thường)
    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE LOWER(s.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    // Kiểm tra tồn tại theo số điện thoại
    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE s.phoneNumber = :phoneNumber")
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    // Kiểm tra tồn tại theo tên nhà cung cấp (không phân biệt chữ hoa thường)
    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE LOWER(s.suplierName) = LOWER(:suplierName)")
    boolean existsBySupplierNameIgnoreCase(@Param("suplierName") String suplierName);

    // Kiểm tra tồn tại theo email nhưng không phải ID hiện tại (cho update)
    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE LOWER(s.email) = LOWER(:email) AND s.suplierId != :id")
    boolean existsByEmailIgnoreCaseAndIdNot(@Param("email") String email, @Param("id") Integer id);

    // Kiểm tra tồn tại theo số điện thoại nhưng không phải ID hiện tại (cho update)
    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE s.phoneNumber = :phoneNumber AND s.suplierId != :id")
    boolean existsByPhoneNumberAndIdNot(@Param("phoneNumber") String phoneNumber, @Param("id") Integer id);
}
