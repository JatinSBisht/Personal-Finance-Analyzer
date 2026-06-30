package com.example.financeanalyzer.service;

import com.example.financeanalyzer.dto.DashboardSummary;
import com.example.financeanalyzer.model.Transaction;
import com.example.financeanalyzer.model.TransactionType;
import com.example.financeanalyzer.model.User;
import com.example.financeanalyzer.repository.CategoryRepository;
import com.example.financeanalyzer.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final MlPredictionService mlPredictionService;

    public TransactionService(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            MlPredictionService mlPredictionService) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.mlPredictionService = mlPredictionService;
    }

    public Transaction save(Transaction transaction, Long categoryId, User user) {
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        transaction.setCategory(category);
        transaction.setUser(user);
        transaction.setType(category.getType());
        return transactionRepository.save(transaction);
    }

    public Transaction findOwned(Long id, User user) {
        var transaction = transactionRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Transaction does not belong to current user");
        }
        return transaction;
    }

    public void delete(Long id, User user) {
        transactionRepository.delete(findOwned(id, user));
    }

    public List<Transaction> search(User user, TransactionType type, String keyword, LocalDate fromDate, LocalDate toDate) {
        String cleanedKeyword = keyword == null || keyword.isBlank() ? null : keyword.trim();
        return transactionRepository.search(user, type, cleanedKeyword, fromDate, toDate);
    }

    public DashboardSummary summary(User user) {
        BigDecimal income = transactionRepository.sumByUserAndType(user, TransactionType.INCOME);
        BigDecimal expense = transactionRepository.sumByUserAndType(user, TransactionType.EXPENSE);
        return new DashboardSummary(
                income,
                expense,
                income.subtract(expense),
                mlPredictionService.predictNextMonthExpense(user),
                mapRows(transactionRepository.expenseByCategory(user), "category", "amount"),
                mapRows(transactionRepository.monthlyExpenses(user), "month", "amount"));
    }

    public List<Transaction> recent(User user) {
        return transactionRepository.findTop5ByUserOrderByTransactionDateDescIdDesc(
                user, org.springframework.data.domain.PageRequest.of(0, 5));
    }

    public Map<String, BigDecimal> monthlySummary(User user, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        Map<String, BigDecimal> summary = new LinkedHashMap<>();
        summary.put("income", transactionRepository.sumByMonthAndType(user, TransactionType.INCOME, start, end));
        summary.put("expense", transactionRepository.sumByMonthAndType(user, TransactionType.EXPENSE, start, end));
        summary.put("balance", summary.get("income").subtract(summary.get("expense")));
        return summary;
    }

    public List<Map<String, Object>> categoryReport(User user) {
        return mapRows(transactionRepository.expenseByCategory(user), "category", "amount");
    }

    private List<Map<String, Object>> mapRows(List<Object[]> rows, String labelKey, String valueKey) {
        return rows.stream()
                .map(row -> Map.of(labelKey, row[0], valueKey, row[1]))
                .toList();
    }
}
