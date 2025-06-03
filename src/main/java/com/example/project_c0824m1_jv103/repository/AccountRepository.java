package com.example.project_c0824m1_jv103.repository;

import com.example.project_c0824m1_jv103.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Employee, Integer> {
    Employee findByEmail(String email);
    Employee findByRole(Employee.Role role);
}
