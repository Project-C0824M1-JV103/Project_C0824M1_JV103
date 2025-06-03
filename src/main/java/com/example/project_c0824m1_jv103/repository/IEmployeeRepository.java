package com.example.project_c0824m1_jv103.repository;

import com.example.project_c0824m1_jv103.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEmployeeRepository extends JpaRepository<Employee, Integer> {
    @Query("SELECT e FROM Employee e WHERE " +
            "(:fullName IS NULL OR e.fullName LIKE %:fullName%) AND " +
            "(:phone IS NULL OR e.phone LIKE %:phone%) AND " +
            "(:role IS NULL OR e.role = :role)")
    List<Employee> searchEmployees(@Param("fullName") String fullName,
                                   @Param("phone") String phone,
                                   @Param("role") Employee.Role role);
}
