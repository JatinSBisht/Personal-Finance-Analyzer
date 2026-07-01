package com.example.financeanalyzer.controller;

import com.example.financeanalyzer.dto.BudgetRequest;
import com.example.financeanalyzer.dto.BudgetStatus;
import com.example.financeanalyzer.dto.DashboardSummary;
import com.example.financeanalyzer.model.Category;
import com.example.financeanalyzer.model.Transaction;
import com.example.financeanalyzer.model.TransactionType;
import com.example.financeanalyzer.repository.CategoryRepository;
import com.example.financeanalyzer.service.BudgetService;
import com.example.financeanalyzer.service.MlPredictionService;
import com.example.financeanalyzer.service.TransactionService;
import com.example.financeanalyzer.service.UserService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final UserService userService;
    private final TransactionService transactionService;
    private final CategoryRepository categoryRepository;
    private final BudgetService budgetService;
    private final MlPredictionService mlPredictionService;

    public ApiController(
            UserService userService,
            TransactionService transactionService,
            CategoryRepository categoryRepository,
            BudgetService budgetService,
            MlPredictionService mlPredictionService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.categoryRepository = categoryRepository;
        this.budgetService = budgetService;
        this.mlPredictionService = mlPredictionService;
    }

    @GetMapping("/dashboard")
    public DashboardSummary dashboard(Principal principal) {
        return transactionService.summary(userService.currentUser(principal.getName()));
    }

    @GetMapping("/transactions")
    public List<Transaction> transactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Principal principal) {
        return transactionService.search(userService.currentUser(principal.getName()), type, keyword, fromDate, toDate);
    }

    @PostMapping("/transactions")
    public Transaction create(@Valid Transaction transaction, @RequestParam Long categoryId, Principal principal) {
        return transactionService.save(transaction, categoryId, userService.currentUser(principal.getName()));
    }

    @DeleteMapping("/transactions/{id}")
    public void delete(@PathVariable Long id, Principal principal) {
        transactionService.delete(id, userService.currentUser(principal.getName()));
    }

    // ---- New: categories ----

    @GetMapping("/categories")
    public List<Category> categories(@RequestParam(required = false) TransactionType type) {
        return type == null ? categoryRepository.findAll() : categoryRepository.findByTypeOrderByName(type);
    }

    // ---- New: monthly income/expense/balance summary ----

    @GetMapping("/reports/monthly")
    public Map<String, BigDecimal> monthlySummary(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            Principal principal) {
        var selectedMonth = month == null ? YearMonth.now() : month;
        return transactionService.monthlySummary(userService.currentUser(principal.getName()), selectedMonth);
    }

    @GetMapping("/reports/categories")
    public List<Map<String, Object>> categoryReport(Principal principal) {
        return transactionService.categoryReport(userService.currentUser(principal.getName()));
    }

    // ---- New: next-month expense prediction ----

    @GetMapping("/prediction/next-month")
    public Map<String, BigDecimal> predictNextMonth(Principal principal) {
        var user = userService.currentUser(principal.getName());
        return Map.of("predictedExpense", mlPredictionService.predictNextMonthExpense(user));
    }

    // ---- New: budgets ----

    @GetMapping("/budget")
    public BudgetStatus budgetStatus(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            Principal principal) {
        var selectedMonth = month == null ? YearMonth.now() : month;
        return budgetService.status(userService.currentUser(principal.getName()), selectedMonth);
    }

    @PostMapping("/budget")
    public BudgetStatus setBudget(@RequestBody BudgetRequest request, Principal principal) {
        var user = userService.currentUser(principal.getName());
        var month = request.month() == null ? YearMonth.now() : YearMonth.parse(request.month());
        budgetService.setBudget(user, month, request.limitAmount());
        return budgetService.status(user, month);
    }
}

