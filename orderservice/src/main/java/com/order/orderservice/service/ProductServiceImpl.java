package com.order.orderservice.service;

import com.order.orderservice.dto.ProductRequest;
import com.order.orderservice.dto.ProductResponse;
import com.order.orderservice.entity.Product;
import com.order.orderservice.exception.ResourceNotFoundException;
import com.order.orderservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product: {}", request.getName());

        Product product = Product.builder()
                .name(request.getName())
                .stock(request.getStock())
                .price(request.getPrice())
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product created with ID: {}", savedProduct.getId());

        return mapToProductResponse(savedProduct, "Product created successfully");
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with ID: " + id));

        return mapToProductResponse(product, "Product retrieved successfully");
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> mapToProductResponse(product, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with ID: " + id));

        product.setName(request.getName());
        product.setStock(request.getStock());
        product.setPrice(request.getPrice());

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated: {}", updatedProduct.getName());

        return mapToProductResponse(updatedProduct, "Product updated successfully");
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted with ID: {}", id);
    }

    @Override
    @Transactional
    public ProductResponse updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with ID: " + id));

        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException(
                    "Cannot reduce stock below 0. Current: " + product.getStock() +
                            ", Attempted reduction: " + quantity);
        }

        product.setStock(newStock);
        Product updatedProduct = productRepository.save(product);
        log.info("Stock updated for product {}: {} -> {}",
                id, product.getStock(), newStock);

        return mapToProductResponse(updatedProduct,
                "Stock updated successfully. New stock: " + newStock);
    }

    private ProductResponse mapToProductResponse(Product product, String message) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .price(product.getPrice())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .message(message)
                .build();
    }
}