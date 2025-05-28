package http.tasktracksystem.domain.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import http.tasktracksystem.domain.exceptions.custom.AlreadyExistsException;
import http.tasktracksystem.domain.exceptions.custom.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.List;

import static http.tasktracksystem.domain.utils.responses.GeneralTemplates.USER_OR_PASSWORD_ARE_NOT_VALID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Invalid JSON enum or type conversion (e.g. wrong enum name in JSON body)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            if (invalidFormatException.getTargetType().isEnum()) {
                Class<?> enumType = invalidFormatException.getTargetType();
                List<String> validValues = Arrays.stream(enumType.getEnumConstants())
                        .map(Object::toString)
                        .toList();

                String message = String.format(
                        "Invalid value '%s' for enum '%s'. Valid values are: %s",
                        invalidFormatException.getValue(),
                        enumType.getSimpleName(),
                        validValues
                );
                log.error("ENUM_VALIDATION_ERROR {}", invalidFormatException.getMessage(), invalidFormatException);
                return ResponseEntity.badRequest().body(new ErrorResponse("ENUM_VALIDATION_ERROR", List.of(message)));
            }
            log.error("INVALID_FORMAT {}", invalidFormatException.getMessage(), invalidFormatException);
            return ResponseEntity.badRequest().body(new ErrorResponse("INVALID_FORMAT", List.of(invalidFormatException.getOriginalMessage())));
        }

        log.error("MESSAGE_NOT_READABLE {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(new ErrorResponse("MESSAGE_NOT_READABLE", List.of(ex.getMessage())));
    }

    // Validation errors on @RequestBody (Bean Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        log.error("VALIDATION_FAILED {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_FAILED", messages));
    }

    // Validation errors on @ModelAttribute, @RequestParam, @PathVariable (e.g., @Valid on query param bean)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        List<String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        log.error("BINDING_FAILED {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("BINDING_FAILED", messages));
    }

    // Validation on single parameters (e.g., @RequestParam @Min(1) int page)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> messages = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        log.error("CONSTRAINT_VIOLATION {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("CONSTRAINT_VIOLATION", messages));
    }

    // Invalid enum in query/path param, or type mismatch (e.g., ?type=wrongValue)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            Object[] validValues = ex.getRequiredType().getEnumConstants();
            String message = String.format(
                    "Invalid value '%s' for enum '%s'. Valid values are: %s",
                    ex.getValue(),
                    ex.getRequiredType().getSimpleName(),
                    Arrays.toString(validValues)
            );

            log.error("ENUM_VALIDATION_ERROR {}", ex.getMessage(), ex);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("ENUM_VALIDATION_ERROR", List.of(message)));
        }

        String message = String.format(
                "Invalid value '%s' for type %s",
                ex.getValue(), ex.getRequiredType()
        );

        log.error("TYPE_MISMATCH {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("TYPE_MISMATCH", List.of(message)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception ex) {
        log.error("INTERNAL_ERROR {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", List.of(ex.getMessage())));
    }

    // 401 - Unauthorized
    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("UNAUTHORIZED", List.of(ex.getMessage())));
    }

    // 403 - Forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("FORBIDDEN", List.of("You are not allowed to perform this action.")));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("CLIENT_ERROR", List.of(USER_OR_PASSWORD_ARE_NOT_VALID)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(IllegalArgumentException ex) {
        log.error("INTERNAL_ERROR {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", List.of(ex.getMessage())));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        log.error("NOT_FOUND {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", List.of(ex.getMessage())));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(AlreadyExistsException ex) {
        log.error("CLIENT_ERROR {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CLIENT_ERROR", List.of(ex.getMessage())));
    }

    public record ErrorResponse(String code, List<String> messages) {
    }
}
