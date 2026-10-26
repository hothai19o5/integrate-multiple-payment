package com.javabuilder.stripe.dto.response;

import lombok.Builder;

@Builder 
public record PaymentGatewayResponse(
    String paymentUrl,
    String gatewayTransactionId
) {}
