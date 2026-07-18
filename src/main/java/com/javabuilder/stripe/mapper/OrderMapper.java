package com.javabuilder.stripe.mapper;

import com.javabuilder.stripe.dto.response.OrderResponse;
import com.javabuilder.stripe.dto.response.OrderItemResponse;
import com.javabuilder.stripe.entity.Order;
import com.javabuilder.stripe.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderItems", source = "orderDetails")
    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemResponse toOrderItemResponse(OrderDetail orderDetail);
}
