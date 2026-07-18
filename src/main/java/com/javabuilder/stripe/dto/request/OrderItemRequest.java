package com.javabuilder.stripe.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull(message = "Product ID cannot be null")
        @Min(value = 1, message = "Product ID must be greater than 0")
        Long productId,

        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {}
