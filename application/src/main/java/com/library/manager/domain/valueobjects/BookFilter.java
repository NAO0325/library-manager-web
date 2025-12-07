package com.library.manager.domain.valueobjects;

import com.library.manager.domain.BookGenre;

public record BookFilter(String title, String author, BookGenre bookGenre, Boolean active) {

    public BookFilter {
        title = normalize(title);
        author = normalize(author);
        active = active != null ? active : true;
    }

    public boolean hasTitle() {
        return title != null;
    }

    public boolean hasAuthor() {
        return author != null;
    }

    public boolean hasGenre() {
        return bookGenre != null;
    }

    private static String normalize(String value) {
        if ( value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
