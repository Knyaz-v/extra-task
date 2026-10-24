package com.extra.task.controller;

import com.extra.task.dto.LoanRequest;
import com.extra.task.model.Loan;
import com.extra.task.service.LibraryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LibraryService service;

    public LoanController(LibraryService service) {
        this.service = service;
    }

    @GetMapping
    public List<Loan> all(@RequestParam(required = false, defaultValue = "false") boolean active) {
        return active ? service.getActiveLoans() : service.getAllLoans();
    }

    @PostMapping("/borrow")
    public Loan borrow(@RequestBody LoanRequest req) {
        return service.borrowBook(req.getBookId(), req.getBorrowerName());
    }

    @PostMapping("/{loanId}/return")
    public Loan returnBook(@PathVariable Long loanId) {
        return service.returnBook(loanId);
    }
}