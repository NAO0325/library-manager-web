package com.library.manager.domain;

import java.time.LocalDateTime;
import java.util.Collection;

public class Editorial {

    private Long id;

    private String name;

    private String address;

    private Long maximumBooks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String email;

    private Collection<Book> books;

}
