package com.library.manager.domain.valueobjects;

import java.util.List;

public record PaginatedResult<T>(
        List<T> content,
        long totalElements,
        int pageNumber,
        int pageSize
) {

    public int totalPages() {
        return totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / pageSize);
    }
}