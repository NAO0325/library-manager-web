package com.library.manager.application.services;

import com.library.manager.application.exceptions.BookNotFoundException;
import com.library.manager.application.ports.driven.BookRepositoryPort;
import com.library.manager.application.ports.driving.BookServicePort;
import com.library.manager.domain.Book;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.domain.valueobjects.PaginationQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookServiceUseCase implements BookServicePort {

    private final BookRepositoryPort bookRepositoryPort;

    @Override
    public Book save(Book book) {
        LocalDateTime now = LocalDateTime.now();
        book.setActive(true);
        book.setCreatedAt(now);
        book.setUpdatedAt(now);

        return bookRepositoryPort.save(book);
    }

    @Override
    public Book findActiveById(Long bookId) {
        validateId(bookId);

        return bookRepositoryPort.findActiveById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
    }

    @Override
    public Book update(Book book) {
        LocalDateTime now = LocalDateTime.now();
        validateId(book.getId());
        getBookIfExist(book.getId());

        book.setUpdatedAt(LocalDateTime.now());

        return bookRepositoryPort.save(book);
    }

    @Override
    public void deactivate(Long bookId) {
        validateId(bookId);
        Book book = getBookIfExist(bookId);

        book.setActive(false);
        book.setUpdatedAt(LocalDateTime.now());

        bookRepositoryPort.save(book);
    }

    @Override
    public PaginatedResult<Book> getAllWithFilters(BookFilter filter, PaginationQuery paginationQuery) {
        return bookRepositoryPort.findAllWithFilters(filter, paginationQuery);
    }

    private void validateId(Long bookId) {
        if (bookId == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }
    }

    private Book getBookIfExist(Long bookId){
        return bookRepositoryPort.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
    }
}
