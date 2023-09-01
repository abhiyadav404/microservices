package com.abhims.ProductService.service;

import com.abhims.ProductService.entity.Product;
import com.abhims.ProductService.exception.ProductServiceCustomException;
import com.abhims.ProductService.model.ProductRequest;
import com.abhims.ProductService.model.ProductResponse;
import com.abhims.ProductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.*;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository repository;
    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding Product...");

        Product product = Product.builder()
                .productName(productRequest.getName())
                .quantity(productRequest.getQuantity())
                .price(productRequest.getPrice())
                .build();

        repository.save(product);
        log.info("Product Created..");
        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {
        log.info("get the product for product id {}", productId);

        Product product=repository.findById(productId)
                .orElseThrow(()->
                        new ProductServiceCustomException("The product with given id is not found", "PRODUCT_NOT_FOUND"));

        ProductResponse response=new ProductResponse();

        copyProperties(product,response);

        return response;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce quantity {} for id {} :",quantity,productId );

        Product product = repository.findById(productId).orElseThrow(
                ()-> new ProductServiceCustomException("Product with iven id is not found","PRODUCT_NOT_FOUND")
        );

        if (product.getQuantity()<quantity)
            throw new ProductServiceCustomException("Product does not have sufficient quantity","INSUFFICIENT_QUANTITY");

       product.setQuantity(product.getQuantity()-quantity);
       repository.save(product);
       log.info("Product quantity updated successfully..");
    }
}
