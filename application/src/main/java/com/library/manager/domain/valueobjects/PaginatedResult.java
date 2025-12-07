package com.library.manager.domain.valueobjects;

import java.util.List;

public record PaginatedResult<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize
) {
}