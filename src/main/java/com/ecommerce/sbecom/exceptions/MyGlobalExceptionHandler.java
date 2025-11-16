package com.ecommerce.sbecom.exceptions;

import com.ecommerce.sbecom.payload.APIResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class MyGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String,String> response= new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error)->{
            String fieldName=((FieldError)error).getField();
            String message= error.getDefaultMessage();
            response.put(fieldName, message);
        });
        return new ResponseEntity<Map<String,String>>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> myConstraintViolationException(ConstraintViolationException e) {
        Map<String, String> response = new HashMap<>();

        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            response.put(fieldName, message);
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myyResourceNotFoundException(ResourceNotFoundException e){
        String message=e.getMessage();
        APIResponse apiResponse= new APIResponse(message,false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIException(APIException e){
        String message=e.getMessage();
        APIResponse apiResponse= new APIResponse(message,false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}
