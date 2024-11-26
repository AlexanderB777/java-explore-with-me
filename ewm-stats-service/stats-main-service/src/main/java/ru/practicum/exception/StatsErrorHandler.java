package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StatsErrorHandler {

    @ExceptionHandler(TimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String timeException(TimeException ex) {
        return ex.getMessage();
    }

}