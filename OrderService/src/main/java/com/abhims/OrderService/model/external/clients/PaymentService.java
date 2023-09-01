package com.abhims.OrderService.model.external.clients;

import com.abhims.OrderService.exception.CustomException;
import com.abhims.OrderService.model.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CircuitBreaker(name = "external", fallbackMethod = "fallBack")
@FeignClient(name = "PAYMENT-SERVICE/payment")
public interface PaymentService {

    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);

    default ResponseEntity<Long>fallBack(Exception exception){
        throw new CustomException("Payment service is not working..!","UNAVAILABLE",500);
    }
}
