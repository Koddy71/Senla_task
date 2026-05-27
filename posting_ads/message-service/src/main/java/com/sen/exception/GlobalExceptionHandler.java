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

    @ExceptionHandler(DialogNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDialogNotFound(DialogNotFoundException ex) {
        logger.warn("Диалог не найден: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DialogAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleDialogAlreadyExists(DialogAlreadyExistsException ex) {
        logger.warn("Диалог уже существует: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(DialogAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleDialogAccessDenied(DialogAccessDeniedException ex) {
        logger.warn("Доступ к диалогу запрещён: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(SelfDialogException.class)
    public ResponseEntity<ErrorResponse> handleSelfDialog(SelfDialogException ex) {
        logger.warn("Попытка создать диалог с самим собой: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<ErrorResponse> handleUserBlocked(UserBlockedException ex) {
        logger.warn("Пользователь заблокирован: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ErrorResponse> handleUserServiceError(UserServiceException ex) {
        logger.error("Ошибка вызова user-service: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Сервис пользователей временно недоступен, повторите попытку позже");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    // ошибки Spring Security 

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        logger.warn("Неудачная попытка аутентификации");
        ErrorResponse body = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Неверный логин или пароль");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        logger.warn("Доступ запрещён: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Недостаточно прав");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // Ошибки валидации и форматов 

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.warn("Неверный тип параметра '{}' ожидался {}", ex.getName(), ex.getRequiredType().getSimpleName());
        String message = String.format("Параметр '%s' должен быть типа %s", ex.getName(),
                ex.getRequiredType().getSimpleName());
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Некорректный аргумент: {}", ex.getMessage());
        String message = "Некорректное значение параметра: " + ex.getMessage();
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        logger.warn("Ошибка валидации полей: {}", ex.getMessage());
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage()
                                : fieldError.getCode(),
                        (e, r) -> e));
        ErrorFullResponse body = new ErrorFullResponse(
                status.value(),
                "Ошибка валидации запроса",
                fieldErrors);
        return ResponseEntity.status(status).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        logger.warn("Некорректный формат тела запроса: {}", ex.getMostSpecificCause().getMessage());
        String message = "Некорректный формат запроса: " + ex.getMostSpecificCause().getMessage();
        ErrorResponse body = new ErrorResponse(status.value(), message);
        return ResponseEntity.status(status).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        logger.warn("Отсутствует параметр: {}", ex.getParameterName());
        String message = "Отсутствует обязательный параметр: " + ex.getParameterName();
        ErrorResponse body = new ErrorResponse(status.value(), message);
        return ResponseEntity.status(status).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        if (status.is5xxServerError()) {
            logger.error("Внутренняя ошибка сервера: {}", request.getDescription(false), ex);
        } else {
            logger.warn("Ошибка запроса: {} - {}", status.value(), ex.getMessage());
        }
        String clientMessage = status.is5xxServerError() ? "Внутренняя ошибка сервера" : ex.getMessage();
        ErrorResponse apiError = new ErrorResponse(status.value(), clientMessage);
        return ResponseEntity.status(status).headers(headers).body(apiError);
    }
}