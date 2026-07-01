package com.example.financeanalyzer.controller;

import com.example.financeanalyzer.service.BudgetService;
import com.example.financeanalyzer.service.UserService;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.YearMonth;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BudgetController {
    private final UserService userService;
    private final BudgetService budgetService;

    public BudgetController(UserService userService, BudgetService budgetService) {
        this.userService = userService;
        this.budgetService = budgetService;
    }

    @GetMapping("/budget")
    public String view(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            Model model,
            Principal principal) {
        var selectedMonth = month == null ? YearMonth.now() : month;
        var user = userService.currentUser(principal.getName());
        model.addAttribute("month", selectedMonth);
        model.addAttribute("status", budgetService.status(user, selectedMonth));
        return "budget";
    }

    @PostMapping("/budget")
    public String save(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @RequestParam BigDecimal limitAmount,
            Principal principal) {
        var user = userService.currentUser(principal.getName());
        budgetService.setBudget(user, month, limitAmount);
        return "redirect:/budget?month=" + month;
    }
}
