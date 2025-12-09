package com.library.manager.driven.repositories;

import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.driven.repositories.models.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookJpaRepository extends JpaRepository<BookEntity, Long> {

    @Query("""
        SELECT b
        FROM BookEntity b
        WHERE (:#{#filter.active} IS NULL OR b.active = :#{#filter.active})
        AND (:#{#filter.title} IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :#{#filter.title}, '%')))
        AND (:#{#filter.author} IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :#{#filter.author}, '%')))
        AND (:#{#filter.bookGenre} IS NULL OR b.bookGenre = :#{#filter.bookGenre})
        """)
    Page<BookEntity> findAllWithFilters(BookFilter filter, Pageable pageable);

    Optional<BookEntity> findByIdAndActiveTrue(Long id);

}
