package com.abhims.OrderService.exception;

import com.abhims.OrderService.model.external.response.ErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Log4j2
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
   public ResponseEntity<ErrorResponse> handleCustomException(CustomException exception){
        log.info("CustomOrder service Exception occured");

        return new ResponseEntity<>(ErrorResponse.builder()
                .errorMessage(exception.getMessage())
                .errorCode(exception.getErrorCode())
                .build(), HttpStatus.valueOf(exception.getStatus()));
   }
}
