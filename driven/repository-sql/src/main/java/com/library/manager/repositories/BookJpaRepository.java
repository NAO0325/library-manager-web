package com.library.manager.repositories;

import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.repositories.models.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface BookJpaRepository extends JpaRepository<BookEntity, Long> {

    @Query("""
        SELECT b
        FROM BookEntity b
        WHERE (:active IS NULL OR b.active = :active)
        AND (:#{#filter.title} IS NULL OR LOWER(CAST(b.title AS string)) LIKE LOWER(CONCAT('%', CAST(:#{#filter.title} AS string), '%')))
        AND (:#{#filter.author} IS NULL OR LOWER(CAST(b.author AS string)) LIKE LOWER(CONCAT('%', CAST(:#{#filter.author} AS string), '%')))
        AND (:#{#filter.bookGenre} IS NULL OR b.bookGenre = :#{#filter.bookGenre})
        """)
    Page<BookEntity> findAllWithFilters(BookFilter filter, Pageable pageable);

}
