package com.finance.ledger.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {

        String message = e.getMessage();

        if (message.contains("권한")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
        }

        if (message.contains("찾을 수 없습니다")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}