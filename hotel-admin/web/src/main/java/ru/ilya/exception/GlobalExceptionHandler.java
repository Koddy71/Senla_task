package ru.ilya.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ru.ilya.exceptions.NotFoundException;
import ru.ilya.exceptions.ValidationException;
import ru.ilya.exceptions.ServiceException;
import ru.ilya.exceptions.RoomException;

import java.util.stream.Collectors;

@ControllerAdvice(basePackages = "ru.ilya.controller")
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        logger.warn("NotFoundException: {}", ex.getMessage());
        ApiError body = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<ApiError> handleValidation(ValidationException ex) {
        logger.warn("ValidationException: {}", ex.getMessage());
        ApiError body = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({ ServiceException.class, RoomException.class })
    protected ResponseEntity<ApiError> handleServiceErrors(RuntimeException ex) {
        logger.error("Service/Room error: {}", ex.getMessage(), ex);
        ApiError body = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ApiError> handleIllegalArg(IllegalArgumentException ex) {
        logger.warn("IllegalArgumentException: {}", ex.getMessage());
        ApiError body = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        logger.warn("HttpMessageNotReadable: {}", ex.getMessage());
        String msg = "Невозможно прочитать тело запроса: " + ex.getMostSpecificCause().getMessage();
        ApiError body = new ApiError(HttpStatus.BAD_REQUEST.value(), msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        String details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": "
                        + (fe.getDefaultMessage() == null ? fe.getCode() : fe.getDefaultMessage()))
                .collect(Collectors.joining("; "));
        logger.warn("MethodArgumentNotValid: {}", details);
        ApiError body = new ApiError(HttpStatus.BAD_REQUEST.value(), "Ошибки валидации: " + details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiError> handleAny(Exception ex) {
        logger.error("Unexpected exception", ex);
        ApiError body = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Внутренняя ошибка сервера: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}