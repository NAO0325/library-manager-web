package com.library.manager.application.ports.driven;

import com.library.manager.domain.Book;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.domain.valueobjects.PaginationQuery;

public interface BookRepositoryPort {

    PaginatedResult<Book> findAllWithFilters(BookFilter filter, PaginationQuery paginationQuery);

}
