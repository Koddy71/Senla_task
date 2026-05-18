package com.sen.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sen.dto.response.ErrorResponse;
import com.sen.dto.response.ErrorFullResponse;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        logger.warn("Пользователь не найден: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        logger.warn("Попытка регистрации с уже существующим логином: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex) {
        logger.warn("Недостаточно средств: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AdServiceException.class)
    public ResponseEntity<ErrorResponse> handleAdServiceError(AdServiceException ex) {
        logger.error("Ошибка внешнего сервиса: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Сервис объявлений временно недоступен, повторите попытку позже");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(AuthenticationRequiredException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationRequired(AuthenticationRequiredException ex) {
        logger.warn("Требуется аутентификация: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(NotOwnerException.class)
    public ResponseEntity<ErrorResponse> handleNotOwner(NotOwnerException ex) {
        logger.warn("Пользователь не является владельцем ресурса: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentError(PaymentException ex) {
        logger.warn("Ошибка платежа: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AdException.class)
    public ResponseEntity<ErrorResponse> handleAdError(AdException ex) {
        logger.warn("Ошибка при работе с объявлением: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        logger.warn("Неудачная попытка входа: неверный логин или пароль");
        ErrorResponse body = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Неверный логин или пароль");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        logger.warn("Доступ запрещён: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                "Недостаточно прав для выполнения операции");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class) // неверный тип параметра запроса
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.warn("Неверный тип параметра: параметр '{}' должен быть типа {}",
                ex.getName(), ex.getRequiredType().getSimpleName());
        String errorMessage = String.format("Параметр '%s' должен быть типа %s",
                ex.getName(), ex.getRequiredType().getSimpleName());
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class) // некорректное значение аргумента в коде
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Некорректное значение аргумента: {}", ex.getMessage());
        String message = "Некорректное значение параметра: " + ex.getMessage();
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) //ошибка валидации
    public ResponseEntity<ErrorFullResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        logger.warn("Ошибка валидации полей запроса: {}", ex.getMessage());

        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage()
                                : fieldError.getCode(),
                        (existing, replacement) -> existing));

        ErrorFullResponse body = new ErrorFullResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка валидации запроса",
                fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class) // некорректный формат тела запроса
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.warn("Некорректный формат тела запроса: {}", ex.getMostSpecificCause().getMessage());
        String message = "Некорректный формат запроса: " + ex.getMostSpecificCause().getMessage();
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex) {
        logger.warn("Отсутствует обязательный параметр запроса: {}", ex.getParameterName());
        String message = "Отсутствует обязательный параметр: " + ex.getParameterName();
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        if (status.is5xxServerError()) {
            logger.error("Внутренняя ошибка сервера при обработке запроса {} {}: {}",
                    request.getDescription(false), ex);
        } else {
            logger.warn("Ошибка при обработке запроса: {} - {}", status.value(), ex.getMessage());
        }

        String clientMessage = status.is5xxServerError() ? "Внутренняя ошибка сервера" : ex.getMessage();
        ErrorResponse apiError = new ErrorResponse(status.value(), clientMessage);
        return ResponseEntity.status(status).headers(headers).body(apiError);
    }
}