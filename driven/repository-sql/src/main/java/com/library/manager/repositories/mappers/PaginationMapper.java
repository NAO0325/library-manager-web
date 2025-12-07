package com.library.manager.repositories.mappers;

import com.library.manager.domain.valueobjects.PaginationQuery;
import org.mapstruct.Mapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Mapper(componentModel = "spring")
public interface PaginationMapper {

    String DEFAULT_SORT_FIELD = "id";
    String DEFAULT_SORT_DIRECTION = "asc";

    default Pageable toPageable(PaginationQuery paginationQuery) {
        Sort.Direction direction = "desc".equalsIgnoreCase(paginationQuery.sortDirection())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, paginationQuery.sortBy());

        return PageRequest.of(
                paginationQuery.page(),
                paginationQuery.pageSize(),
                sort
        );
    }

    default PaginationQuery toPaginationQuery(Pageable pageable) {
        final String sortBy = extractSortField(pageable);
        final String sortDir = extractSortDirection(pageable);

        return new PaginationQuery(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortBy,
                sortDir
        );
    }

    private String extractSortField(final Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            return pageable.getSort()
                    .iterator()
                    .next()
                    .getProperty();
        }
        return DEFAULT_SORT_FIELD;
    }

    private String extractSortDirection(final Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            return pageable.getSort()
                    .iterator()
                    .next().
                    getDirection()
                    .name()
                    .toLowerCase();
        }
        return DEFAULT_SORT_DIRECTION;
    }

}
