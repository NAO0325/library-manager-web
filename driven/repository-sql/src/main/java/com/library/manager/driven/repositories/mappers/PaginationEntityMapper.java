package com.library.manager.driven.repositories.mappers;

import com.library.manager.domain.valueobjects.PaginationQuery;
import org.mapstruct.Mapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Mapper(componentModel = "spring")
public interface PaginationEntityMapper {

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
}
