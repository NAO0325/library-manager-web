package com.library.manager.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    private Long id;

    private Editorial editorial;

    private String author;

    private String title;

    private BookGenre bookGenre;

    private Integer pages;

    private Integer publicationYear;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean active;

}
