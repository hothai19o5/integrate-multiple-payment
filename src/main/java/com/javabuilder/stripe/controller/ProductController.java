package com.javabuilder.stripe.controller;

import com.javabuilder.stripe.dto.request.CreateProductRequest;
import com.javabuilder.stripe.dto.response.ApiResponse;
import com.javabuilder.stripe.dto.response.CreateProductResponse;
import com.javabuilder.stripe.dto.response.PageResponse;
import com.javabuilder.stripe.dto.response.ProductDetailResponse;
import com.javabuilder.stripe.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    ApiResponse<CreateProductResponse> createProduct(@RequestBody @Valid CreateProductRequest request) {
        var data = productService.createProduct(request);
        return ApiResponse.<CreateProductResponse>builder()
                .status("success")
                .message("Product created successfully")
                .data(data)
                .build();
    }

    @GetMapping
    ApiResponse<PageResponse<ProductDetailResponse>> getProducts(@RequestParam(required = false, defaultValue = "1") int page,
                                                                 @RequestParam(required = false, defaultValue = "10") int size
    ) {
        var data = productService.getProducts(page, size);
        return ApiResponse.<PageResponse<ProductDetailResponse>>builder()
                .status("success")
                .message("Products retrieved successfully")
                .data(data)
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<ProductDetailResponse> getProductById(@PathVariable Long id) {
        var data = productService.getProductById(id);
        return ApiResponse.<ProductDetailResponse>builder()
                .status("success")
                .message("Product retrieved successfully")
                .data(data)
                .build();
    }
}
