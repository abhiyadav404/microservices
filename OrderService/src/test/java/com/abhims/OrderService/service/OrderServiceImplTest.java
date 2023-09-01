package com.abhims.OrderService.service;

import com.abhims.OrderService.entity.Order;
import com.abhims.OrderService.exception.CustomException;
import com.abhims.OrderService.model.*;
import com.abhims.OrderService.model.external.clients.PaymentService;
import com.abhims.OrderService.model.external.clients.ProductService;
import com.abhims.OrderService.model.external.response.PaymentResponse;
import com.abhims.OrderService.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceImplTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private ProductService productService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();

    @DisplayName("Get Order - Success Scenario")
    @Test
    void test_When_Order_Success(){
        //Mocking
        Order order = getMockOrder();

        when(repository.findById(anyLong())).thenReturn(Optional.of(order));

        when(restTemplate
                .getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(), ProductResponse.class))
                .thenReturn(getMockProductResponse());

        when(restTemplate.getForObject
                ("http://PAYMENT-SERVICE/payment/order/"+order.getId(), PaymentResponse.class))
        .thenReturn(getMockPaymentResponse());
        //Actual
        OrderResponse orderResponse = orderService.getOrderDetails(1);
        //Verify

        verify(repository,times(1)).findById(anyLong());
        verify(restTemplate,times(1))
                .getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(), ProductResponse.class);
        verify(restTemplate,times(1))
                .getForObject
                        ("http://PAYMENT-SERVICE/payment/order/"+order.getId(), PaymentResponse.class);
        //Assert
        assertNotNull(orderResponse);
        assertEquals(order.getId(),orderResponse.getOrderId());


    }

    @DisplayName("Get Orders - Failure Scenario")
    @Test
    void test_When_Get_Order_NOT_FOUND_then_Not_Found(){
        //Mocking
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        //Actual
        CustomException exception = assertThrows(CustomException.class,
                ()->orderService.getOrderDetails(1));
        //Assert
        assertEquals("NOT_FOUND",exception.getErrorCode());
        assertEquals(404,exception.getStatus());

        //Verify
        verify(repository,times(1)).findById(anyLong());
    }

    @DisplayName("Place Order Success Scenario")
    @Test
    void test_When_Place_Order_Success(){
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(repository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<Long>(1L,HttpStatus.OK));

        long orderId = orderService.placeOrder(orderRequest);

        verify(repository,times(2)).save(any());
        verify(productService,times(1)).reduceQuantity(anyLong(),anyLong());
        verify(paymentService,times(1)).doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(),orderId);

    }

    @DisplayName("Place Order Payment Failed Scenario")
    @Test
    void test_when_Place_Order_Payment_Fails_then_Order_Placed(){

        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(repository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());

        long orderId = orderService.placeOrder(orderRequest);

        verify(repository,times(2)).save(any());
        verify(productService,times(1)).reduceQuantity(anyLong(),anyLong());
        verify(paymentService,times(1)).doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(),orderId);

    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1).quantity(10).totalAmount(100).paymentMode(PaymentMode.CASH)
                .build();
    }

    private PaymentResponse getMockPaymentResponse() {
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .paymentId(1).paymentDate(Instant.now()).paymentMode(PaymentMode.CASH)
                .orderId(1).amount(200).status("ACCEPTED")
                .build();
        return paymentResponse;
    }

    private ProductResponse getMockProductResponse() {
        ProductResponse productResponse = ProductResponse.builder()
                .productName("IPhone").productId(2).price(5000).quantity(200)
                .build();
        return productResponse;
    }

    private Order getMockOrder() {
        return Order.builder()
                .quantity(200).orderDate(Instant.now()).orderStatus("Created").productId(2).amount(5000).id(1)
                .build();
    }
}