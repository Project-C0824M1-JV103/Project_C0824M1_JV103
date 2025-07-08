package com.example.project_c0824m1_jv103.repository;

import com.example.project_c0824m1_jv103.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ISaleRepository extends JpaRepository<Sale, Integer> {
    
    // Đếm số đơn hàng theo customer
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.customer.customerId = :customerId")
    Long countSalesByCustomerId(@Param("customerId") Integer customerId);

    // Đếm số đơn hàng theo ngày
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate >= :dateStart AND s.saleDate < :dateEnd")
    int countSalesByDate(@Param("dateStart") java.time.LocalDateTime dateStart, @Param("dateEnd") java.time.LocalDateTime dateEnd);

    // Tổng tiền theo ngày
    @Query("SELECT SUM(s.amount) FROM Sale s WHERE s.saleDate >= :dateStart AND s.saleDate < :dateEnd")
    java.math.BigDecimal sumAmountByDate(@Param("dateStart") java.time.LocalDateTime dateStart, @Param("dateEnd") java.time.LocalDateTime dateEnd);

    // Đếm số đơn hàng theo tháng
    @Query("SELECT COUNT(s) FROM Sale s WHERE YEAR(s.saleDate) = :year AND MONTH(s.saleDate) = :month")
    int countSalesByMonth(@Param("year") int year, @Param("month") int month);

    // Tổng tiền theo tháng
    @Query("SELECT SUM(s.amount) FROM Sale s WHERE YEAR(s.saleDate) = :year AND MONTH(s.saleDate) = :month")
    java.math.BigDecimal sumAmountByMonth(@Param("year") int year, @Param("month") int month);
    List<Sale> findByCustomer_CustomerId(Integer customerId);
}
