package com.example.kpi.exceptions;

import com.example.kpi.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Глобальный обработчик исключений.
 * <p>
 * Предоставляет централизованную обработку исключений,
 * возникающих в приложении. Возвращает структурированные ответы
 * об ошибках в формате JSON.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение KpiNotFoundException.
     * <p>
     * Возвращает HTTP-статус 404 и сообщение об отсутствии KPI.
     *
     * @param ex Исключение KpiNotFoundException.
     * @return ResponseEntity с данными об ошибке.
     */

    /**
     * Обрабатывает любое общее исключение.
     * <p>
     * Возвращает HTTP-статус 500 и сообщение о внутренней ошибке сервера.
     *
     * @param ex Исключение, содержащее информацию о непредвиденной ошибке.
     * @return ResponseEntity с данными об ошибке.
     */


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGenericException(ResourceNotFoundException ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getBindingResult().getFieldError().getDefaultMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResourceExistException.class)
    public ResponseEntity<ErrorResponse> handleResourceExist(ResourceExistException ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
