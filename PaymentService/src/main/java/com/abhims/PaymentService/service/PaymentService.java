package com.abhims.PaymentService.service;

import com.abhims.PaymentService.model.PaymentRequest;
import com.abhims.PaymentService.model.PaymentResponse;

public interface PaymentService {
    Long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(String orderId);
}
