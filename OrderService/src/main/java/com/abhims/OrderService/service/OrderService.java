package com.abhims.OrderService.service;

import com.abhims.OrderService.model.OrderRequest;
import com.abhims.OrderService.model.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest request);

    OrderResponse getOrderDetails(long orderId);
}
