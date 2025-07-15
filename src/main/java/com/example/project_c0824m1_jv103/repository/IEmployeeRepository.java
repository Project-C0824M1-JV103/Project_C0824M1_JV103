package com.example.project_c0824m1_jv103.repository;

import com.example.project_c0824m1_jv103.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            "(:role IS NULL OR e.role = :role) AND e.role != com.example.project_c0824m1_jv103.model.Employee.Role.Admin")
    List<Employee> searchEmployees(@Param("fullName") String fullName,
                                   @Param("phone") String phone,
                                   @Param("role") Employee.Role role);

    @Query("SELECT e FROM Employee e WHERE " +
            "(:fullName IS NULL OR e.fullName LIKE %:fullName%) AND " +
            "(:phone IS NULL OR e.phone LIKE %:phone%) AND " +
            "(:role IS NULL OR e.role = :role) AND e.role != com.example.project_c0824m1_jv103.model.Employee.Role.Admin")
    Page<Employee> searchEmployeesWithPaging(@Param("fullName") String fullName,
                                             @Param("phone") String phone,
                                             @Param("role") Employee.Role role,
                                             Pageable pageable);


    Employee findByEmail(String email);
    Employee findByPhone(String phone);
    List<Employee> findByRole (Employee.Role role);
    @Query("SELECT e FROM Employee e WHERE e.role != com.example.project_c0824m1_jv103.model.Employee.Role.Admin")
    Page<Employee> findAllNonAdminWithPaging(Pageable pageable);
    
    // Validation methods for edit employee (excluding current employee)
    boolean existsByEmailIgnoreCaseAndEmployeeIdNot(String email, Integer employeeId);
    boolean existsByPhoneAndEmployeeIdNot(String phone, Integer employeeId);

    // Kiểm tra tồn tại theo email nhưng loại trừ employee hiện tại
    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE LOWER(e.email) = LOWER(:email) AND e.employeeId != :employeeId")
    boolean existsByEmailIgnoreCaseAndCustomerIdNot(@Param("email") String email, @Param("employeeId") Integer employeeId);

    // Kiểm tra tồn tại theo số điện thoại nhưng loại trừ employee hiện tại
    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.phone = :phoneNumber AND e.employeeId != :employeeId")
    boolean existsByPhoneNumberAndCustomerIdNot(@Param("phoneNumber") String phoneNumber, @Param("employeeId") Integer employeeId);

    // ========== SYSTEM-WIDE VALIDATION METHODS ==========
    
    /**
     * Kiểm tra email có tồn tại trong toàn hệ thống không (Employee, Customer, Supplier)
     */
    @Query(value = "SELECT COUNT(*) FROM (" +
            "SELECT email FROM employee WHERE LOWER(email) = LOWER(:email) " +
            "UNION " +
            "SELECT email FROM customer WHERE LOWER(email) = LOWER(:email) " +
            "UNION " +
            "SELECT email FROM supplier WHERE LOWER(email) = LOWER(:email)" +
            ") AS combined_emails", nativeQuery = true)
    Long countEmailExistsInSystem(@Param("email") String email);

    /**
     * Kiểm tra phone có tồn tại trong toàn hệ thống không (Employee, Customer, Supplier)
     */
    @Query(value = "SELECT COUNT(*) FROM (" +
            "SELECT phone FROM employee WHERE phone = :phone " +
            "UNION " +
            "SELECT phone_number FROM customer WHERE phone_number = :phone " +
            "UNION " +
            "SELECT phone_number FROM supplier WHERE phone_number = :phone" +
            ") AS combined_phones", nativeQuery = true)
    Long countPhoneExistsInSystem(@Param("phone") String phone);
}
