package com.arits.expense_trancker.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<R> {

    private int status;
    private String message;
    private LocalDateTime timestamp;
    private R result;
    private Object error;


}
