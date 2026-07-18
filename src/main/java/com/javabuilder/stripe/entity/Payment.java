package com.javabuilder.stripe.entity;

import com.javabuilder.stripe.common.PaymentGateway;
import com.javabuilder.stripe.common.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Payment extends AbstractEntity<Long> {

    private String description;

    private String gatewayTransactionId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentGateway paymentGateway;

    @Column(nullable = false)
    private BigDecimal amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}
