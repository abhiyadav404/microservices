package com.abhims.ProductService.exception;

import lombok.Builder;
import lombok.Data;

@Data

public class ProductServiceCustomException extends RuntimeException{
    private String errorCode;
    public ProductServiceCustomException(String message, String errorCode){
      super(message);
      this.errorCode=errorCode;
    }
}
