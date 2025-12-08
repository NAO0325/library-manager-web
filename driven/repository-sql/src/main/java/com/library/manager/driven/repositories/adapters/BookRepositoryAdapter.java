package com.library.manager.driven.repositories.adapters;

import com.library.manager.application.ports.driven.BookRepositoryPort;
import com.library.manager.domain.Book;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.domain.valueobjects.PaginationQuery;
import com.library.manager.driven.repositories.BookJpaRepository;
import com.library.manager.driven.repositories.mappers.BookEntityMapper;
import com.library.manager.driven.repositories.mappers.PaginationEntityMapper;
import com.library.manager.driven.repositories.models.BookEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@RequiredArgsConstructor
@Service
public class BookRepositoryAdapter implements BookRepositoryPort {

    private final BookJpaRepository bookJpaRepository;

    private final BookEntityMapper bookEntityMapper;

    private final PaginationEntityMapper paginationEntityMapper;

    @Override
    public Book save(Book book) {

        BookEntity bookEntity = bookEntityMapper.toEntity(book);

        return bookEntityMapper.toDomain(bookJpaRepository.save(bookEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findActiveById(Long bookId) {
        return bookJpaRepository.findByIdAndActiveTrue(bookId)
                .map(bookEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(Long bookId) {
        return bookJpaRepository.findById(bookId)
                .map(bookEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<Book> findAllWithFilters(BookFilter filter, PaginationQuery paginationQuery) {

        Pageable pageable = paginationEntityMapper.toPageable(paginationQuery);

        Page<BookEntity> entityPage = bookJpaRepository.findAllWithFilters(filter, pageable);

        return bookEntityMapper.toBookPaginatedResult(entityPage);
    }
}
