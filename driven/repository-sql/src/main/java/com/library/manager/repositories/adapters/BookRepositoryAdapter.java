package com.library.manager.repositories.adapters;

import com.library.manager.application.ports.driven.BookRepositoryPort;
import com.library.manager.domain.Book;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.domain.valueobjects.PaginationQuery;
import com.library.manager.repositories.BookJpaRepository;
import com.library.manager.repositories.mappers.BookEntityMapper;
import com.library.manager.repositories.mappers.PaginationMapper;
import com.library.manager.repositories.models.BookEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookRepositoryAdapter implements BookRepositoryPort {

    private final BookJpaRepository bookJpaRepository;

    private final BookEntityMapper bookEntityMapper;

    private final PaginationMapper paginationMapper;

    @Override
    public PaginatedResult<Book> findAllWithFilters(BookFilter filter, PaginationQuery paginationQuery) {

        Pageable pageable = paginationMapper.toPageable(paginationQuery);

        Page<BookEntity> entityPage = bookJpaRepository.findAllWithFilters(filter, pageable);

        return bookEntityMapper.toBookPaginatedResult(entityPage);
    }
}
