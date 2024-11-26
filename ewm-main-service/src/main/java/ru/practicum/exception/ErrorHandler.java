package ru.practicum.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.util.UtilConstants;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.info(e.getMessage());
        return new ApiError(e.getMessage(),
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST,
                UtilConstants.getCurrentTimeFormatted());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        log.info(e.getMessage());
        return new ApiError(e.getMessage(),
                "The required object was not found",
                HttpStatus.NOT_FOUND,
                UtilConstants.getCurrentTimeFormatted());
    }

    @ExceptionHandler(PSQLException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handlePSQLException(PSQLException e) {
        log.info(e.getMessage());
        return new ApiError(e.getMessage(),
                "Integrity constraint has been violated.",
                HttpStatus.CONFLICT,
                UtilConstants.getCurrentTimeFormatted());
    }

    @ExceptionHandler(EventConstraintException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEventConstraintException(EventConstraintException e) {
        log.info(e.getMessage());
        return new ApiError(e.getMessage(),
                "For the requested operation the conditions are not met.",
                HttpStatus.FORBIDDEN,
                UtilConstants.getCurrentTimeFormatted());
    }

    @ExceptionHandler(IncorrectRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectRequestException(IncorrectRequestException e) {
        log.info(e.getMessage());
        return new ApiError(e.getMessage(),
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST,
                UtilConstants.getCurrentTimeFormatted());
    }
}