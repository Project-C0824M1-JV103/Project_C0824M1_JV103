package com.example.project_c0824m1_jv103.repository;

import com.example.project_c0824m1_jv103.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ISaleRepository extends JpaRepository<Sale, Integer> {
    
    // Đếm số đơn hàng theo customer
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.customer.customerId = :customerId")
    Long countSalesByCustomerId(@Param("customerId") Integer customerId);
}
