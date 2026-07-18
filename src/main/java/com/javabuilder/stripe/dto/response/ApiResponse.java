package com.javabuilder.stripe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record ApiResponse<T>(
        String status,
        String message,
        T data
) {
}
