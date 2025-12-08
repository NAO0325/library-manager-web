package com.library.manager.application.ports.driven;

import com.library.manager.domain.Book;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.domain.valueobjects.PaginationQuery;

import java.util.Optional;

public interface BookRepositoryPort {

    Book save(Book Book);

    Optional<Book> findById(Long bookId);

    Optional<Book> findActiveById(Long bookId);

    PaginatedResult<Book> findAllWithFilters(BookFilter filter, PaginationQuery paginationQuery);

}
