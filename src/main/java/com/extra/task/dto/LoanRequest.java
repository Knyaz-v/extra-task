package com.extra.task.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanRequest {
    private Long bookId;
    private String borrowerName;
}