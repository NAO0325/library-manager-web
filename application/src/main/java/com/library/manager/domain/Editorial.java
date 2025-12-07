package com.library.manager.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
