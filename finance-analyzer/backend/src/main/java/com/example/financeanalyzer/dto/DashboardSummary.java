package com.example.financeanalyzer.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardSummary(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal currentBalance,
        BigDecimal predictedNextMonthExpense,
        List<Map<String, Object>> expenseByCategory,
        List<Map<String, Object>> monthlyExpenses) {
}
