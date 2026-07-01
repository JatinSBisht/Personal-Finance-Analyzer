package com.example.financeanalyzer.service;

import com.example.financeanalyzer.dto.BudgetStatus;
import com.example.financeanalyzer.model.Budget;
import com.example.financeanalyzer.model.TransactionType;
import com.example.financeanalyzer.model.User;
import com.example.financeanalyzer.repository.BudgetRepository;
import com.example.financeanalyzer.repository.TransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(BudgetRepository budgetRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    public Budget setBudget(User user, YearMonth month, BigDecimal limitAmount) {
        String key = month.toString();
        Budget budget = budgetRepository.findByUserAndYearMonth(user, key).orElseGet(Budget::new);
        budget.setUser(user);
        budget.setYearMonth(key);
        budget.setLimitAmount(limitAmount);
        return budgetRepository.save(budget);
    }

    public BudgetStatus status(User user, YearMonth month) {
        String key = month.toString();
        BigDecimal limit = budgetRepository.findByUserAndYearMonth(user, key)
                .map(Budget::getLimitAmount)
                .orElse(BigDecimal.ZERO);

        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        BigDecimal spent = transactionRepository.sumByMonthAndType(user, TransactionType.EXPENSE, start, end);

        BigDecimal remaining = limit.subtract(spent);
        double percentUsed = limit.compareTo(BigDecimal.ZERO) == 0
                ? 0.0
                : spent.divide(limit, 4, RoundingMode.HALF_UP).doubleValue() * 100;

        return new BudgetStatus(key, limit, spent, remaining, percentUsed, spent.compareTo(limit) > 0 && limit.compareTo(BigDecimal.ZERO) > 0);
    }
}
