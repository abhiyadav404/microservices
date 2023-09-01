package com.abhims.ProductService.service;

import com.abhims.ProductService.model.ProductRequest;
import com.abhims.ProductService.model.ProductResponse;

public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}
