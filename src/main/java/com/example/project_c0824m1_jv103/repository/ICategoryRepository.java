package com.example.project_c0824m1_jv103.repository;

import com.example.project_c0824m1_jv103.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Integer> {
}
