package com.example.financeanalyzer.service;

import com.example.financeanalyzer.model.User;
import com.example.financeanalyzer.repository.TransactionRepository;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MlPredictionService {
    private final TransactionRepository transactionRepository;
    private final String pythonCommand;
    private final String scriptPath;

    public MlPredictionService(
            TransactionRepository transactionRepository,
            @Value("${app.ml.python-command}") String pythonCommand,
            @Value("${app.ml.script-path}") String scriptPath) {
        this.transactionRepository = transactionRepository;
        this.pythonCommand = pythonCommand;
        this.scriptPath = scriptPath;
    }

    public BigDecimal predictNextMonthExpense(User user) {
        var monthlyValues = transactionRepository.monthlyExpenses(user).stream()
                .map(row -> row[1].toString())
                .collect(Collectors.joining(","));
        if (monthlyValues.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            Process process = new ProcessBuilder(pythonCommand, scriptPath, monthlyValues)
                    .redirectErrorStream(true)
                    .start();
            boolean finished = process.waitFor(Duration.ofSeconds(5).toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);
            if (!finished || process.exitValue() != 0) {
                return fallback(monthlyValues);
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                return new BigDecimal(reader.readLine()).max(BigDecimal.ZERO);
            }
        } catch (Exception ex) {
            return fallback(monthlyValues);
        }
    }

    private BigDecimal fallback(String csv) {
        String[] values = csv.split(",");
        BigDecimal total = BigDecimal.ZERO;
        for (String value : values) {
            total = total.add(new BigDecimal(value));
        }
        return total.divide(BigDecimal.valueOf(values.length), 2, java.math.RoundingMode.HALF_UP);
    }
}
