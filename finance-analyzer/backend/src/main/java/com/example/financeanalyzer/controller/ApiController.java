package com.example.financeanalyzer.controller;

import com.example.financeanalyzer.dto.DashboardSummary;
import com.example.financeanalyzer.model.Transaction;
import com.example.financeanalyzer.model.TransactionType;
import com.example.financeanalyzer.service.TransactionService;
import com.example.financeanalyzer.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final UserService userService;
    private final TransactionService transactionService;

    public ApiController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
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
}
