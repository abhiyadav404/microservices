package com.abhims.CloudGateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallBackController {

    @GetMapping("/orderServiceFallBack")
    public String orderServiceFallBack(){
        return "Order service is currently Down !";
    }

    @GetMapping("/paymentServiceFallBack")
    public String paymentServiceFallBack(){
        return "Payment service is currently Down !";
    }

    @GetMapping("/productServiceFallBack")
    public String productServiceFallBack(){
        return "Product service is currently Down !";
    }
}
