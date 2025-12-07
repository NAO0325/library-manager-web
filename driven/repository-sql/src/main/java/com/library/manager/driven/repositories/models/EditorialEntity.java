package com.library.manager.driven.repositories.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Data
@Builder
@Table(name = "EDITORIAL")
@NoArgsConstructor
@AllArgsConstructor
public class EditorialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID",  nullable = false)
    private Long id;

    @Size(max = 150)
    @Column(name = "NAME", nullable = false)
    private String name;

    @NotNull
    @Size(max = 120)
    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @NotNull
    @Column(name = "MAXIMUM_BOOKS", nullable = false)
    private Long maximumBooks;

    @NotNull
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @Size(max = 100)
    @Column(name = "EMAIL")
    private String email;
}
