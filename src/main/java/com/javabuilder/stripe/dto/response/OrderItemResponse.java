package com.javabuilder.stripe.dto.response;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record OrderItemResponse(
        Long productId,
        String productName,
        BigDecimal price,
        Integer quantity
) {}
