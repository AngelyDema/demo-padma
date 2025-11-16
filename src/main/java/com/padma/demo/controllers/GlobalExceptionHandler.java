package com.padma.demo.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<Map<String, String>> handleTypeMismatch(
                        MethodArgumentTypeMismatchException ex) {

                String error = String.format("Invalid parameter '%s': expected %s but got '%s'",
                                ex.getName(),
                                ex.getRequiredType().getSimpleName(),
                                ex.getValue());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("error", error));
        }
}
