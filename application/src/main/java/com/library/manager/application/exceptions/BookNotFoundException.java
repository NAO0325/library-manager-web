package com.library.manager.application.exceptions;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long bookId) {
        super(String.format("Book not found for ID: %d", bookId));
    }
}
