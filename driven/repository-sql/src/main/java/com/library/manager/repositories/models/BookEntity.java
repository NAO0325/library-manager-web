package com.library.manager.repositories.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne
    @JoinColumn(name = "EDITORIAL_ID", nullable = false)
    private EditorialEntity editorial;

    @Size(max = 250)
    @Column(name = "AUTHOR", nullable = false)
    private String author;

    @Size(max = 200)
    @Column(name = "TITLE")
    private String title;

    @Size(max = 150)
    @Column(name = "GENRE")
    private String genre;

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
