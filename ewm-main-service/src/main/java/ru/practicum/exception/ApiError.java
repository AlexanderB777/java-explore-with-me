package ru.practicum.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(String message,
                       String reason,
                       HttpStatus status,
                       String timestamp) {
}