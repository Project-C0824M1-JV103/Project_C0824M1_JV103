package com.example.project_c0824m1_jv103.repository;

import com.example.project_c0824m1_jv103.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer, Integer> {
    @Query("SELECT c FROM Customer c WHERE c.customerName LIKE %:keyword% OR c.phoneNumber LIKE %:keyword%")
    List<Customer> searchByNameOrPhone(@Param("keyword") String keyword);
    
    // Pagination methods
    Page<Customer> findAll(Pageable pageable);
    
    @Query("SELECT c FROM Customer c WHERE c.customerName LIKE %:keyword% OR c.phoneNumber LIKE %:keyword%")
    Page<Customer> searchByNameOrPhone(@Param("keyword") String keyword, Pageable pageable);
    
    // Improved separate search for name and phone with better Java logic
    @Query("SELECT c FROM Customer c WHERE " +
           "(:customerName IS NULL OR :customerName = '' OR UPPER(c.customerName) LIKE UPPER(CONCAT('%', :customerName, '%'))) AND " +
           "(:phoneNumber IS NULL OR :phoneNumber = '' OR c.phoneNumber LIKE CONCAT('%', :phoneNumber, '%'))")
    Page<Customer> searchByNameAndPhone(@Param("customerName") String customerName, 
                                        @Param("phoneNumber") String phoneNumber, 
                                        Pageable pageable);
    
    // Additional search methods for better Java backend search
    @Query("SELECT c FROM Customer c WHERE UPPER(c.customerName) LIKE UPPER(CONCAT('%', :name, '%'))")
    Page<Customer> findByCustomerNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT c FROM Customer c WHERE c.phoneNumber LIKE CONCAT('%', :phone, '%')")
    Page<Customer> findByPhoneNumberContaining(@Param("phone") String phone, Pageable pageable);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    // Kiểm tra tồn tại theo email (không phân biệt chữ hoa thường)
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE LOWER(c.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    // Kiểm tra tồn tại theo email nhưng loại trừ customer hiện tại
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE LOWER(c.email) = LOWER(:email) AND c.customerId != :customerId")
    boolean existsByEmailIgnoreCaseAndCustomerIdNot(@Param("email") String email, @Param("customerId") Integer customerId);

    // Kiểm tra tồn tại theo số điện thoại
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.phoneNumber = :phoneNumber")
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    // Kiểm tra tồn tại theo số điện thoại nhưng loại trừ customer hiện tại
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.phoneNumber = :phoneNumber AND c.customerId != :customerId")
    boolean existsByPhoneNumberAndCustomerIdNot(@Param("phoneNumber") String phoneNumber, @Param("customerId") Integer customerId);
}
