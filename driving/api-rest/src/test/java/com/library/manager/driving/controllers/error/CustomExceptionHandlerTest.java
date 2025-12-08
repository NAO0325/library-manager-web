package com.library.manager.driving.controllers.error;

import com.library.manager.application.exceptions.BookNotFoundException;
import com.library.manager.driving.controllers.models.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomExceptionHandler Tests")
class CustomExceptionHandlerTest {

    @InjectMocks
    private CustomExceptionHandler customExceptionHandler;

    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        webRequest = mock(WebRequest.class);
    }

    @Nested
    @DisplayName("handleBookNotFound() tests")
    class HandleBookNotFoundTests {

        @Test
        @DisplayName("Should return 404 NOT_FOUND for BookNotFoundException")
        void shouldReturn404ForBookNotFoundException() {
            // Arrange
            BookNotFoundException exception = new BookNotFoundException(1L);

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleBookNotFound(exception, webRequest);

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Should set NOT_FOUND error code")
        void shouldSetNotFoundErrorCode() {
            // Arrange
            BookNotFoundException exception = new BookNotFoundException(1L);

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleBookNotFound(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertEquals(Error.CodeEnum.NOT_FOUND, response.getBody().getCode());
        }

        @Test
        @DisplayName("Should include book ID in error message")
        void shouldIncludeBookIdInMessage() {
            // Arrange
            BookNotFoundException exception = new BookNotFoundException(123L);

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleBookNotFound(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertTrue(response.getBody().getMessage().contains("123"));
        }

        @Test
        @DisplayName("Should include resource type in details")
        void shouldIncludeResourceTypeInDetails() {
            // Arrange
            BookNotFoundException exception = new BookNotFoundException(1L);

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleBookNotFound(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getDetails());
            assertEquals("Book", response.getBody().getDetails().get("resource"));
        }

        @Test
        @DisplayName("Should set timestamp in UTC")
        void shouldSetTimestampInUtc() {
            // Arrange
            BookNotFoundException exception = new BookNotFoundException(1L);

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleBookNotFound(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getTimestamp());
            assertEquals(ZoneOffset.UTC, response.getBody().getTimestamp().getOffset());
            assertEquals(0, response.getBody().getTimestamp().getNano());
        }
    }

    @Nested
    @DisplayName("handleIllegalArgument() tests")
    class HandleIllegalArgumentTests {

        @Test
        @DisplayName("Should return 400 BAD_REQUEST for IllegalArgumentException")
        void shouldReturn400ForIllegalArgumentException() {
            // Arrange
            IllegalArgumentException exception = new IllegalArgumentException("Invalid input");

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleIllegalArgument(exception, webRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("Should set INVALID_CRITERIA error code")
        void shouldSetInvalidCriteriaErrorCode() {
            // Arrange
            IllegalArgumentException exception = new IllegalArgumentException("Invalid input");

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleIllegalArgument(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertEquals(Error.CodeEnum.INVALID_CRITERIA, response.getBody().getCode());
        }

        @Test
        @DisplayName("Should include exception message in error response")
        void shouldIncludeExceptionMessage() {
            // Arrange
            IllegalArgumentException exception = new IllegalArgumentException("Test error message");

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleIllegalArgument(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertEquals("Test error message", response.getBody().getMessage());
        }

        @Test
        @DisplayName("Should set timestamp in UTC")
        void shouldSetTimestampInUtc() {
            // Arrange
            IllegalArgumentException exception = new IllegalArgumentException("Test");

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleIllegalArgument(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getTimestamp());
            assertEquals(ZoneOffset.UTC, response.getBody().getTimestamp().getOffset());
            assertEquals(0, response.getBody().getTimestamp().getNano());
        }

        @Test
        @DisplayName("Should add details when message contains 'criteria'")
        void shouldAddDetailsForCriteriaMessage() {
            // Arrange
            IllegalArgumentException exception = new IllegalArgumentException("Invalid criteria provided");

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleIllegalArgument(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getDetails());
            Map<String, Object> details = response.getBody().getDetails();
            assertEquals("criteria", details.get("field"));
            assertEquals("Invalid criteria or parameter provided", details.get("issue"));
        }

        @Test
        @DisplayName("Should not add details when message does not contain 'criteria'")
        void shouldNotAddDetailsWhenNoCriteria() {
            // Arrange
            IllegalArgumentException exception = new IllegalArgumentException("Some other error");

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleIllegalArgument(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertNull(response.getBody().getDetails());
        }

        @Test
        @DisplayName("Should handle null message")
        void shouldHandleNullMessage() {
            // Arrange
            IllegalArgumentException exception = new IllegalArgumentException();

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleIllegalArgument(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertNull(response.getBody().getDetails());
        }
    }

    @Nested
    @DisplayName("handleValidationExceptions() tests")
    class HandleValidationExceptionsTests {

        @Test
        @DisplayName("Should return 400 BAD_REQUEST for validation errors")
        void shouldReturn400ForValidationErrors() {
            // Arrange
            BindingResult bindingResult = mock(BindingResult.class);
            MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(List.of());

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleValidationExceptions(exception, webRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("Should set VALIDATION_ERROR code")
        void shouldSetValidationErrorCode() {
            // Arrange
            BindingResult bindingResult = mock(BindingResult.class);
            MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(List.of());

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleValidationExceptions(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertEquals(Error.CodeEnum.VALIDATION_ERROR, response.getBody().getCode());
        }

        @Test
        @DisplayName("Should include field errors in details")
        void shouldIncludeFieldErrorsInDetails() {
            // Arrange
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError1 = new FieldError("book", "title", "Title is required");
            FieldError fieldError2 = new FieldError("book", "author", "Author is required");
            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

            MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleValidationExceptions(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getDetails());
            @SuppressWarnings("unchecked")
            Map<String, String> fieldErrors = (Map<String, String>) response.getBody().getDetails().get("fieldErrors");
            assertNotNull(fieldErrors);
            assertEquals("Title is required", fieldErrors.get("title"));
            assertEquals("Author is required", fieldErrors.get("author"));
        }

        @Test
        @DisplayName("Should have default message for invalid request data")
        void shouldHaveDefaultMessage() {
            // Arrange
            BindingResult bindingResult = mock(BindingResult.class);
            MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(List.of());

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleValidationExceptions(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertEquals("Invalid request data", response.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("handleJsonParsingErrors() tests")
    class HandleJsonParsingErrorsTests {

        @Test
        @DisplayName("Should return 400 BAD_REQUEST for JSON parsing errors")
        void shouldReturn400ForJsonParsingErrors() {
            // Arrange
            HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleJsonParsingErrors(exception, webRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("Should set INVALID_JSON error code")
        void shouldSetInvalidJsonErrorCode() {
            // Arrange
            HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleJsonParsingErrors(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertEquals(Error.CodeEnum.INVALID_JSON, response.getBody().getCode());
        }

        @Test
        @DisplayName("Should have descriptive error message")
        void shouldHaveDescriptiveErrorMessage() {
            // Arrange
            HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleJsonParsingErrors(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertEquals("Invalid JSON format or missing required fields", response.getBody().getMessage());
        }

        @Test
        @DisplayName("Should include issue details")
        void shouldIncludeIssueDetails() {
            // Arrange
            HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleJsonParsingErrors(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getDetails());
            assertEquals("Request body is not valid JSON or is missing required fields",
                    response.getBody().getDetails().get("issue"));
        }
    }

    @Nested
    @DisplayName("handleTypeMismatch() tests")
    class HandleTypeMismatchTests {

        @Test
        @DisplayName("Should return 400 BAD_REQUEST for type mismatch")
        void shouldReturn400ForTypeMismatch() {
            // Arrange
            MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
            when(exception.getName()).thenReturn("id");
            when(exception.getValue()).thenReturn("invalid");
            when(exception.getRequiredType()).thenReturn((Class) Long.class);

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleTypeMismatch(exception, webRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("Should set INVALID_PARAMETER error code")
        void shouldSetInvalidParameterErrorCode() {
            // Arrange
            MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
            when(exception.getName()).thenReturn("id");
            when(exception.getValue()).thenReturn("invalid");
            when(exception.getRequiredType()).thenReturn((Class) Long.class);

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleTypeMismatch(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertEquals(Error.CodeEnum.INVALID_PARAMETER, response.getBody().getCode());
        }

        @Test
        @DisplayName("Should include parameter name in message")
        void shouldIncludeParameterNameInMessage() {
            // Arrange
            MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
            when(exception.getName()).thenReturn("bookId");
            when(exception.getValue()).thenReturn("abc");
            when(exception.getRequiredType()).thenReturn((Class) Long.class);

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleTypeMismatch(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertTrue(response.getBody().getMessage().contains("bookId"));
        }

        @Test
        @DisplayName("Should include parameter details")
        void shouldIncludeParameterDetails() {
            // Arrange
            MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
            when(exception.getName()).thenReturn("id");
            when(exception.getValue()).thenReturn("abc");
            when(exception.getRequiredType()).thenReturn((Class) Long.class);

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleTypeMismatch(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            Map<String, Object> details = response.getBody().getDetails();
            assertEquals("id", details.get("parameter"));
            assertEquals("abc", details.get("providedValue"));
            assertEquals("Long", details.get("expectedType"));
        }

        @Test
        @DisplayName("Should handle null required type safely")
        void shouldHandleNullRequiredType() {
            // Arrange
            MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
            when(exception.getName()).thenReturn("id");
            when(exception.getValue()).thenReturn("abc");
            when(exception.getRequiredType()).thenReturn(null);

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleTypeMismatch(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertEquals("unknown", response.getBody().getDetails().get("expectedType"));
        }
    }

    @Nested
    @DisplayName("handleRuntimeException() tests")
    class HandleRuntimeExceptionTests {

        @Test
        @DisplayName("Should return 500 INTERNAL_SERVER_ERROR for RuntimeException")
        void shouldReturn500ForRuntimeException() {
            // Arrange
            RuntimeException exception = new RuntimeException("Unexpected error");

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleRuntimeException(exception, webRequest);

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }

        @Test
        @DisplayName("Should set INTERNAL_ERROR code")
        void shouldSetInternalErrorCode() {
            // Arrange
            RuntimeException exception = new RuntimeException("Unexpected error");

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleRuntimeException(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertEquals(Error.CodeEnum.INTERNAL_ERROR, response.getBody().getCode());
        }

        @Test
        @DisplayName("Should include exception type in details")
        void shouldIncludeExceptionTypeInDetails() {
            // Arrange
            RuntimeException exception = new RuntimeException("Test error");

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleRuntimeException(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertEquals("RuntimeException", response.getBody().getDetails().get("type"));
        }

        @Test
        @DisplayName("Should handle other RuntimeExceptions")
        void shouldHandleOtherRuntimeExceptions() {
            // Arrange
            RuntimeException exception = new IllegalStateException("Invalid state");

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleRuntimeException(exception, webRequest);

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("IllegalStateException", response.getBody().getDetails().get("type"));
        }

        @Test
        @DisplayName("Should have generic error message")
        void shouldHaveGenericErrorMessage() {
            // Arrange
            RuntimeException exception = new RuntimeException("Detailed internal error");

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleRuntimeException(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertEquals("An unexpected error occurred while processing the request",
                    response.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("handleAllExceptions() tests")
    class HandleAllExceptionsTests {

        @Test
        @DisplayName("Should return 500 INTERNAL_SERVER_ERROR for generic exceptions")
        void shouldReturn500ForGenericExceptions() {
            // Arrange
            Exception exception = new Exception("Generic error");

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleAllExceptions(exception, webRequest);

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }

        @Test
        @DisplayName("Should set INTERNAL_ERROR code")
        void shouldSetInternalErrorCode() {
            // Arrange
            Exception exception = new Exception("Generic error");

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleAllExceptions(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertEquals(Error.CodeEnum.INTERNAL_ERROR, response.getBody().getCode());
        }

        @Test
        @DisplayName("Should have generic error message")
        void shouldHaveGenericErrorMessage() {
            // Arrange
            Exception exception = new Exception("Specific internal error");

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleAllExceptions(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertEquals("An unexpected error occurred while processing the request",
                    response.getBody().getMessage());
        }

        @Test
        @DisplayName("Should set timestamp")
        void shouldSetTimestamp() {
            // Arrange
            Exception exception = new Exception("Test");

            // Act
            ResponseEntity<Error> response = customExceptionHandler.handleAllExceptions(exception, webRequest);

            // Assert
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getTimestamp());
            assertTrue(response.getBody().getTimestamp().isBefore(OffsetDateTime.now().plusSeconds(1)));
        }
    }
}
