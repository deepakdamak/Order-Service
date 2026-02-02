package com.order.orderservice.service;

import com.order.orderservice.dto.ProductRequest;
import com.order.orderservice.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse getProductById(Long id);
    List<ProductResponse> getAllProducts();
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    ProductResponse updateStock(Long id, Integer quantity);
}