package com.javabuilder.stripe.strategy.dto;

import java.math.BigDecimal;
import java.util.List;

import com.javabuilder.stripe.common.Currency;

import lombok.Builder;

@Builder 
public record PaymentGatewayCommand(
    String orderCode,
    BigDecimal totalPrice,
    Currency currency,
    String clientIp,
    List<LineItem> lineItems
) {
    @Builder 
    public record LineItem (
        String name, 
        Integer quantity,
        BigDecimal unitPrice
    ) {}
}
