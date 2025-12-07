package com.library.manager.domain.valueobjects;


public record PaginationQuery(int page, int pageSize, String sortBy, String sortDirection) {

    public PaginationQuery {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative.");
        }

        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be at least 1.");
        }

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "title";
        }

        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = "asc";
        }
    }

    public PaginationQuery(int page, int pageSize) {
        this(page, pageSize, "title", "asc");
    }
}