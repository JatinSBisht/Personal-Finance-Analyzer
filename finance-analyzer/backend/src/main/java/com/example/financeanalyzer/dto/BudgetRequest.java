package com.example.financeanalyzer.dto;

import java.math.BigDecimal;

public record BudgetRequest(String month, BigDecimal limitAmount) {
}
