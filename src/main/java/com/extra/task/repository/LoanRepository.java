package com.extra.task.repository;

import com.extra.task.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByBookIdAndReturnedAtIsNull(Long bookId);
    List<Loan> findByReturnedAtIsNull();
}