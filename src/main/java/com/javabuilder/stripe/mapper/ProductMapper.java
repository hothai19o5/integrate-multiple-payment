package com.javabuilder.stripe.mapper;

import com.javabuilder.stripe.dto.request.CreateProductRequest;
import com.javabuilder.stripe.dto.response.CreateProductResponse;
import com.javabuilder.stripe.dto.response.ProductDetailResponse;
import com.javabuilder.stripe.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toProduct(CreateProductRequest request);

    CreateProductResponse toCreateProductResponse(Product product);

    ProductDetailResponse toProductDetailResponse(Product product);
}
