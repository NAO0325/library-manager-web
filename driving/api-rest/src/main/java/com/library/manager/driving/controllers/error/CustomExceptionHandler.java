package com.library.manager.driving.controllers.error;

import com.library.manager.driving.controllers.models.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Component
public class CustomExceptionHandler {

    private static final String FIELD = "field";
    private static final String ISSUE = "issue";
    private static final String CRITERIA_FIELD = "criteria";

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Error> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        Error error = new Error();
        error.setCode(Error.CodeEnum.INVALID_CRITERIA);
        error.setMessage(ex.getMessage());
        error.setTimestamp(nowToUtcOffsetDateTime());

        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains(CRITERIA_FIELD)) {
            Map<String, Object> details = new HashMap<>();
            details.put(FIELD, CRITERIA_FIELD);
            details.put(ISSUE, "Invalid criteria or parameter provided");
            error.setDetails(details);
        } else {
            error.setDetails(null);
        }

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Error> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Error error = new Error();
        error.setCode(Error.CodeEnum.VALIDATION_ERROR);
        error.setMessage("Invalid request data");
        error.setTimestamp(nowToUtcOffsetDateTime());

        Map<String, Object> details = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(objectError -> {
            if (objectError instanceof FieldError fieldError) {
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
        });

        details.put("fieldErrors", fieldErrors);
        error.setDetails(details);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Error> handleJsonParsingErrors(HttpMessageNotReadableException ex, WebRequest request) {
        Error error = new Error();
        error.setCode(Error.CodeEnum.INVALID_JSON);
        error.setMessage("Invalid JSON format or missing required fields");
        error.setTimestamp(nowToUtcOffsetDateTime());

        Map<String, Object> details = new HashMap<>();
        details.put(ISSUE, "Request body is not valid JSON or is missing required fields");

        error.setDetails(details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Error> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        Error error = new Error();
        error.setCode(Error.CodeEnum.INVALID_PARAMETER);
        error.setMessage("Parameter '" + ex.getName() + "' has invalid type");
        error.setTimestamp(nowToUtcOffsetDateTime());

        Map<String, Object> details = new HashMap<>();
        details.put("parameter", ex.getName());
        details.put("providedValue", ex.getValue());

        // Verificar null de forma más segura
        Class<?> requiredType = ex.getRequiredType();
        String expectedType = (requiredType != null) ? requiredType.getSimpleName() : "unknown";
        details.put("expectedType", expectedType);

        error.setDetails(details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Error> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Error error = new Error();
        error.setCode(Error.CodeEnum.INTERNAL_ERROR);
        error.setMessage("An unexpected error occurred while processing the request");
        error.setTimestamp(nowToUtcOffsetDateTime());

        Map<String, Object> details = new HashMap<>();
        details.put("type", ex.getClass().getSimpleName());
        error.setDetails(details);

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleAllExceptions(Exception ex, WebRequest request) {
        Error error = new Error();
        error.setCode(Error.CodeEnum.INTERNAL_ERROR);
        error.setMessage("An unexpected error occurred while processing the request");
        error.setTimestamp(nowToUtcOffsetDateTime());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private OffsetDateTime nowToUtcOffsetDateTime() {
        return OffsetDateTime.now().withNano(0).withOffsetSameInstant(ZoneOffset.UTC);
    }
}
