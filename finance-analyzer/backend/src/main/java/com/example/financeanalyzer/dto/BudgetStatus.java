package com.example.financeanalyzer.dto;

import java.math.BigDecimal;

public record BudgetStatus(
        String yearMonth,
        BigDecimal limitAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        double percentUsed,
        boolean overBudget) {
}
