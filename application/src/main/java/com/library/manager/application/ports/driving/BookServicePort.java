package com.library.manager.application.ports.driving;

import com.library.manager.domain.Book;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.domain.valueobjects.PaginationQuery;

public interface BookServicePort {

    PaginatedResult<Book> getAllWithFilters(BookFilter filter, PaginationQuery paginationQuery);
}
