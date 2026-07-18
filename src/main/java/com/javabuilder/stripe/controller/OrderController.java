package com.javabuilder.stripe.controller;

import com.javabuilder.stripe.dto.request.CreateOrderRequest;
import com.javabuilder.stripe.dto.response.ApiResponse;
import com.javabuilder.stripe.dto.response.OrderResponse;
import com.javabuilder.stripe.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    ApiResponse<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        var data = orderService.createOrder(request);
        return ApiResponse.<OrderResponse>builder()
                .status("success")
                .message("Order created successfully")
                .data(data)
                .build();
    }


    @GetMapping("/{id}")
    ApiResponse<OrderResponse> getOrderById(@PathVariable Long id) {
        var data = orderService.getOrderById(id);
        return ApiResponse.<OrderResponse>builder()
                .status("success")
                .message("Order retrieved successfully")
                .data(data)
                .build();
    }

    @GetMapping("/code/{orderCode}")
    ApiResponse<OrderResponse> getOrderByCode(@PathVariable String orderCode) {
        var data = orderService.getOrderByCode(orderCode);
        return ApiResponse.<OrderResponse>builder()
                .status("success")
                .message("Order retrieved successfully")
                .data(data)
                .build();
    }
}
