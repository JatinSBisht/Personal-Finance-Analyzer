package com.example.financeanalyzer.controller;

import com.example.financeanalyzer.service.TransactionService;
import com.example.financeanalyzer.service.UserService;
import java.security.Principal;
import java.time.YearMonth;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReportController {
    private final UserService userService;
    private final TransactionService transactionService;

    public ReportController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @GetMapping("/reports/monthly")
    public String monthly(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            Model model,
            Principal principal) {
        var selectedMonth = month == null ? YearMonth.now() : month;
        var user = userService.currentUser(principal.getName());
        model.addAttribute("month", selectedMonth);
        model.addAttribute("summary", transactionService.monthlySummary(user, selectedMonth));
        return "reports/monthly";
    }

    @GetMapping("/reports/categories")
    public String categories(Model model, Principal principal) {
        var user = userService.currentUser(principal.getName());
        model.addAttribute("categoryReport", transactionService.categoryReport(user));
        return "reports/categories";
    }
}
