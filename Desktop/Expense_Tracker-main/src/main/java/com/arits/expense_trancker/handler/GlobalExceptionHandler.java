package com.arits.expense_trancker.handler;

import jakarta.validation.constraints.Null;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {


@ExceptionHandler(RuntimeException.class)
public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException e){

    ApiResponse<Object> response= ApiResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(e.getMessage())
            .timestamp(LocalDateTime.now())
            .result(null)
            .error("RUNTIME_ERROR")
            .build();
return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
}

@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ApiResponse<Object>> handleValidatonErrors(MethodArgumentNotValidException e){
    ApiResponse<Object> response= ApiResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("Validation Failed")
            .timestamp(LocalDateTime.now())
            .result(null)
            .error(e.getBindingResult().getFieldError().getDefaultMessage())
            .build();
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
}


}
