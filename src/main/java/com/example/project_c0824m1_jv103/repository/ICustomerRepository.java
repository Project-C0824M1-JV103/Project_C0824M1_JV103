package com.example.project_c0824m1_jv103.repository;

import com.example.project_c0824m1_jv103.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer, Integer> {
    @Query("SELECT c FROM Customer c WHERE c.customerName LIKE %:keyword% OR c.phoneNumber LIKE %:keyword%")
    List<Customer> searchByNameOrPhone(@Param("keyword") String keyword);
}
