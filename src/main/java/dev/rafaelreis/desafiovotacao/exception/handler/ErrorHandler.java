package dev.rafaelreis.desafiovotacao.exception.handler;

import dev.rafaelreis.desafiovotacao.exception.ResourceNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@SuppressWarnings("unused")
@RestControllerAdvice
public class ErrorHandler {

    private final MessageSource messageSource;
    private final WebRequest webRequest;

    public ErrorHandler(MessageSource messageSource, WebRequest webRequest) {
        this.messageSource = messageSource;
        this.webRequest = webRequest;
    }

    private ErrorMessage getMessage(FieldError field) {
        return new ErrorMessage(this.messageSource.getMessage(field, LocaleContextHolder.getLocale()));
    }

    @ExceptionHandler(value = {RuntimeException.class, Exception.class})
    public ResponseEntity<ErrorResponse> serverError(ResourceNotFoundException e) {
        ErrorMessage error = new ErrorMessage(e.getMessage());
        String requestUrl = webRequest.getDescription(false);
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), e.getMessage(), requestUrl, List.of(error));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<ErrorResponse> resourceNotFound(ResourceNotFoundException e) {
        ErrorMessage error = new ErrorMessage(e.getMessage());
        String requestUrl = webRequest.getDescription(false);
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), e.getMessage(), requestUrl, List.of(error));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String target = webRequest.getDescription(false);
        List<ErrorMessage> errors = e.getFieldErrors().stream()
                .map(this::getMessage)
                .toList();

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), "Informações Inválidas", target, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}