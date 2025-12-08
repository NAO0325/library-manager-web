package com.library.manager.driven.repositories.mappers;

import com.library.manager.domain.Book;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.driven.repositories.models.BookEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;


@Mapper(componentModel = "spring")
public interface BookEntityMapper {

    Book toDomain(BookEntity bookEntity);

    BookEntity toEntity(Book book);

    default PaginatedResult<Book> toBookPaginatedResult(Page<BookEntity> entityPage) {
        Page<Book> bookPage = entityPage.map(this::toDomain);

        return new PaginatedResult<>(
                bookPage.getContent(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.getNumber(),
                bookPage.getSize());
    }
}
