package com.example.financeanalyzer.repository;

import com.example.financeanalyzer.model.Budget;
import com.example.financeanalyzer.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByUserAndYearMonth(User user, String yearMonth);
}
