package com.library.manager.driving.controllers.mappers;

import com.library.manager.domain.Book;
import com.library.manager.domain.BookGenre;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.driving.controllers.models.BookResource;
import com.library.manager.driving.controllers.models.BooksResponse;
import com.library.manager.driving.controllers.models.Pagination;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "bookGenre", target = "genre", qualifiedByName = "EnumToString")
    BookResource toBookResource(Book book);

    List<BookResource> toListBookResource(List<Book> bookList);

    @Mapping(source = "pageNumber", target = "number")
    @Mapping(source = "pageSize", target = "size")
    Pagination toPagination(PaginatedResult<Book> paginatedResult);

    default BookFilter toFilter(String author, String title, BookGenre bookGenre) {
        return new BookFilter(title, author, bookGenre, true);
    }

    default BooksResponse toResponse(PaginatedResult<Book> paginatedResult) {
        BooksResponse response = new BooksResponse();

        response.setBooks(toListBookResource(paginatedResult.content()));
        response.setPagination(toPagination(paginatedResult));
        response.getPagination().setTimestamp(nowToUtcOffsetDateTime());

        return response;
    }

    @Named("EnumToString")
    default String enumToString(BookGenre bookGenre) {
        return bookGenre.getDisplayName();
    }

    private OffsetDateTime nowToUtcOffsetDateTime() {
        return OffsetDateTime.now().withNano(0).withOffsetSameInstant(ZoneOffset.UTC);
    }
}
