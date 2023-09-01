package com.abhims.OrderService.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    private long productId;
    private long totalAmount;
    private long quantity;
    private PaymentMode paymentMode;
}
