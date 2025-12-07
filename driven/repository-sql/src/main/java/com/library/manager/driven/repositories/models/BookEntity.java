package com.library.manager.driven.repositories.models;

import com.library.manager.domain.BookGenre;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@Table(name = "BOOK")
@NoArgsConstructor
@AllArgsConstructor
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EDITORIAL_ID", nullable = false)
    private EditorialEntity editorial;

    @Size(max = 250)
    @Column(name = "AUTHOR", nullable = false)
    private String author;

    @Size(max = 250)
    @Column(name = "TITLE")
    private String title;

    @Size(max = 150)
    @Enumerated(EnumType.STRING)
    @Column(name = "GENRE")
    private BookGenre bookGenre;

    @Column(name = "PAGES")
    private Integer pages;

    @Column(name = "PUBLICATION_YEAR")
    private Integer publicationYear;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "ACTIVE", nullable = false)
    private Boolean active;

}
