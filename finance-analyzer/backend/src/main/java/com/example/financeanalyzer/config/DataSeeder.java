package com.example.financeanalyzer.config;

import com.example.financeanalyzer.model.Category;
import com.example.financeanalyzer.model.TransactionType;
import com.example.financeanalyzer.repository.CategoryRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private final CategoryRepository categoryRepository;

    public DataSeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }
        categoryRepository.saveAll(List.of(
                new Category("Salary", TransactionType.INCOME),
                new Category("Freelance", TransactionType.INCOME),
                new Category("Investments", TransactionType.INCOME),
                new Category("Food", TransactionType.EXPENSE),
                new Category("Rent", TransactionType.EXPENSE),
                new Category("Transport", TransactionType.EXPENSE),
                new Category("Shopping", TransactionType.EXPENSE),
                new Category("Utilities", TransactionType.EXPENSE),
                new Category("Health", TransactionType.EXPENSE),
                new Category("Education", TransactionType.EXPENSE)));
    }
}
