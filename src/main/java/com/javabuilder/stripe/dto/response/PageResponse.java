package com.javabuilder.stripe.dto.response;

import lombok.*;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PageResponse<T> {
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;

    @Builder.Default
    private List<T> content = Collections.emptyList();
}
