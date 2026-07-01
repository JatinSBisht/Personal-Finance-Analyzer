package com.example.financeanalyzer.controller;

import com.example.financeanalyzer.service.TransactionService;
import com.example.financeanalyzer.service.UserService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final UserService userService;
    private final TransactionService transactionService;

    public DashboardController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        var user = userService.currentUser(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("summary", transactionService.summary(user));
        model.addAttribute("recentTransactions", transactionService.recent(user));
        return "dashboard";
    }
}
