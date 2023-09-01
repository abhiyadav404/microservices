package com.abhims.OrderService.service;

import com.abhims.OrderService.entity.Order;
import com.abhims.OrderService.exception.CustomException;
import com.abhims.OrderService.model.OrderResponse;
import com.abhims.OrderService.model.PaymentRequest;
import com.abhims.OrderService.model.ProductResponse;
import com.abhims.OrderService.model.external.clients.PaymentService;
import com.abhims.OrderService.model.external.clients.ProductService;
import com.abhims.OrderService.model.OrderRequest;
import com.abhims.OrderService.model.external.response.PaymentResponse;
import com.abhims.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository repository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;
    @Override
    public long placeOrder(OrderRequest request) {

        //ORDER ENTITY -> Save the data with status order Created.
        //PRODUCT SERVICE -> block the products (Reduce the quantity)
        //Payment SERVICE -> payment -> success -> Complete else
        //CANCELLED

        log.info("placing the order request {}: ",request);

        productService.reduceQuantity(request.getProductId(), request.getQuantity());

        log.info("creating order with status CREATED");

        Order order = Order.builder()
                .amount(request.getTotalAmount())
                .orderStatus("CREATED")
                .productId(request.getProductId())
                .orderDate(Instant.now())
                .quantity(request.getQuantity())
                .build();

        order=repository.save(order);

        log.info("Calling Payment Service to complete the payment");

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId()).paymentMode(request.getPaymentMode())
                .amount(request.getTotalAmount())
                .build();

        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done Successfully. Changing the order status to PLACED");
            orderStatus="PLACED";
        }
        catch (Exception e){
         log.error("Error occured during Payment Changing status to PAYMENT_FAILED");
         orderStatus ="PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        repository.save(order);
        log.info("Order placed Successfully with Order id: {}", order.getId());

        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order Details for Order Id :{}",orderId);
        Order order = repository.findById(orderId)
                .orElseThrow(()->new CustomException("Order not found for the order Id "+orderId, "NOT_FOUND",404));

        log.info("Invoking Product Service to fetch the product details for id :{}",order.getProductId());

      ProductResponse productResponse =  restTemplate
                .getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(),ProductResponse.class);

      log.info("Getting the payment information from the Payment Service :");

        PaymentResponse paymentResponse = restTemplate.getForObject
                ("http://PAYMENT-SERVICE/payment/order/"+order.getId(),PaymentResponse.class);

      OrderResponse.ProductDetails productDetails= OrderResponse.ProductDetails.builder()
              .productName(productResponse.getProductName())
              .productId(productResponse.getProductId())
              .quantity(productResponse.getQuantity())
              .price(productResponse.getPrice())
              .build();

      OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails.builder()
              .paymentId(paymentResponse.getPaymentId())
              .paymentStatus(paymentResponse.getStatus())
              .paymentDate(paymentResponse.getPaymentDate())
              .paymentMode(paymentResponse.getPaymentMode())
              .build();

        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getId()).orderStatus(order.getOrderStatus())
                .amount(order.getAmount()).orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();

        return orderResponse;
    }
}
