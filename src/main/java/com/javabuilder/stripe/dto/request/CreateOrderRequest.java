package com.javabuilder.stripe.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateOrderRequest(
        @NotEmpty(message = "Order items cannot be empty")
        @Valid
        List<OrderItemRequest> items
) {}
