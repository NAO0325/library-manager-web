package com.library.manager.application.services;

import com.library.manager.application.ports.driving.BookServicePort;
import com.library.manager.domain.Book;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.domain.valueobjects.PaginationQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceUseCase implements BookServicePort {

    @Override
    public PaginatedResult<Book> getAllWithFilters(BookFilter filter, PaginationQuery paginationQuery) {
        return null;
    }
}
