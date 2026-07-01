package com.example.financeanalyzer.repository;

import com.example.financeanalyzer.model.Category;
import com.example.financeanalyzer.model.TransactionType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByTypeOrderByName(TransactionType type);
}
