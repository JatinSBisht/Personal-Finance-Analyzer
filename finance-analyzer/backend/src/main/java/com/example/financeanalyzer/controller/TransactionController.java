package com.example.financeanalyzer.controller;

import com.example.financeanalyzer.model.Transaction;
import com.example.financeanalyzer.model.TransactionType;
import com.example.financeanalyzer.repository.CategoryRepository;
import com.example.financeanalyzer.service.TransactionService;
import com.example.financeanalyzer.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/transactions")
public class TransactionController {
    private final UserService userService;
    private final TransactionService transactionService;
    private final CategoryRepository categoryRepository;

    public TransactionController(
            UserService userService,
            TransactionService transactionService,
            CategoryRepository categoryRepository) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model,
            Principal principal) {
        var user = userService.currentUser(principal.getName());
        model.addAttribute("transactions", transactionService.search(user, type, keyword, fromDate, toDate));
        model.addAttribute("types", TransactionType.values());
        return "transactions/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        loadFormData(model);
        return "transactions/form";
    }

    @PostMapping
    public String save(
            @Valid @ModelAttribute Transaction transaction,
            BindingResult bindingResult,
            @RequestParam Long categoryId,
            Model model,
            Principal principal) {
        if (bindingResult.hasErrors()) {
            loadFormData(model);
            return "transactions/form";
        }
        var user = userService.currentUser(principal.getName());
        transactionService.save(transaction, categoryId, user);
        return "redirect:/transactions";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model, Principal principal) {
        var user = userService.currentUser(principal.getName());
        model.addAttribute("transaction", transactionService.findOwned(id, user));
        loadFormData(model);
        return "transactions/form";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        var user = userService.currentUser(principal.getName());
        transactionService.delete(id, user);
        return "redirect:/transactions";
    }

    private void loadFormData(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
    }
}
