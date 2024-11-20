package pumrapee.repairmanagementapi.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import pumrapee.repairmanagementapi.exceptions.ErrorResponse;
import pumrapee.repairmanagementapi.exceptions.IOExceptionHandler;
import pumrapee.repairmanagementapi.exceptions.ItemNotFoundException;

import java.util.List;

@RestControllerAdvice
@CrossOrigin(origins = {"http://ip23kk2.sit.kmutt.ac.th", "http://localhost:5173", "http://intproj23.sit.kmutt.ac.th", "https://intproj23.sit.kmutt.ac.th"})
public class GlobalExceptionHandler {

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class,
            HandlerMethodValidationException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationExceptions(Exception exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation error. Check 'errors' field for details.",
                request.getDescription(false)
        );

        if (exception instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException ex = (MethodArgumentTypeMismatchException) exception;
            errorResponse.addValidationError(ex.getName(), ex.getValue() + " is not a valid " + ex.getParameter().getParameterType() +" value");
        } else if (exception instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) exception;
            for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
                errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
            }
        } else if (exception instanceof HandlerMethodValidationException) {
            HandlerMethodValidationException ex = (HandlerMethodValidationException) exception;
            List<ParameterValidationResult> paramNames = ex.getAllValidationResults();
            for (ParameterValidationResult param : paramNames) {
                errorResponse.addValidationError(
                        param.getMethodParameter().getParameterName(),
                        param.getResolvableErrors().get(0).getDefaultMessage()
                                + " (" + param.getArgument().toString() + ")"
                );
            }
        } else if (exception instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException ex = (MissingServletRequestParameterException) exception;
            errorResponse.addValidationError(ex.getParameterName(), "Missing required parameter of type " + ex.getParameterType());
        }

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleRequiredRequestBody
            (Exception exception, WebRequest request) {
        return buildErrorResponse(exception, "Required request body is missing", HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleAuthenticaionException
            (Exception exception, WebRequest request) {
        return buildErrorResponse(exception, "Username or Password is incorrect.", HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({NoResourceFoundException.class, HttpRequestMethodNotSupportedException.class, ItemNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundExceptionException
            (Exception exception, WebRequest request) {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(IOExceptionHandler.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleIOException
            (Exception exception, WebRequest request) {
        return buildErrorResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResponseEntity<ErrorResponse> handleAllUncaughtException
//            (Exception exception, WebRequest request) {
//        return buildErrorResponse(exception, "Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request);
//    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception exception, HttpStatus httpStatus, WebRequest request) {
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception exception, String message, HttpStatus httpStatus, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message, request.getDescription(false).substring(4));
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
}