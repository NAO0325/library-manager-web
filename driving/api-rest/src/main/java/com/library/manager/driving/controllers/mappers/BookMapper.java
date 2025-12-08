package com.library.manager.driving.controllers.mappers;

import com.library.manager.domain.Book;
import com.library.manager.domain.BookGenre;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.driving.controllers.models.*;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface BookMapper {

    @AfterMapping
    default void addLinks(@MappingTarget BookResponse response, Book book) {
        List<Link> links = new ArrayList<>();
        String baseUrl = "/v1/books/" + book.getId();

        links.add(createLink("self", baseUrl, "GET"));
        links.add(createLink("update", baseUrl, "PUT"));

        if (Boolean.TRUE.equals(book.getActive())) {
            links.add(createLink("deactivate", baseUrl, "DELETE"));
        }

        response.setLinks(links);
    }

    @Mapping(source = "bookGenre", target = "bookGenre", qualifiedByName = "EnumToString")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "LocalDateTimeToOffsetDateTime")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "LocalDateTimeToOffsetDateTime")
    BookResponse toBookResponse(Book book);

    @Mapping(source = "bookGenre", target = "bookGenre", qualifiedByName = "StringToEnum")
    Book toBook(BookRequest bookRequest);

    List<BookResponse> toListBookResponse(List<Book> bookList);

    @Mapping(source = "pageNumber", target = "number")
    @Mapping(source = "pageSize", target = "size")
    Pagination toPagination(PaginatedResult<Book> paginatedResult);

    default BookFilter toFilter(String author, String title, BookGenre bookGenre) {
        return new BookFilter(title, author, bookGenre, true);
    }

    default BooksResponse toResponse(PaginatedResult<Book> paginatedResult) {
        BooksResponse response = new BooksResponse();

        response.setBooks(toListBookResponse(paginatedResult.content()));
        response.setPagination(toPagination(paginatedResult));
        response.getPagination().setTimestamp(nowToUtcOffsetDateTime());
        List<Link> links = new ArrayList<>();

        String baseUrl = "/v1/books";
        int page = paginatedResult.pageNumber();
        int size = paginatedResult.pageSize();
        int total = paginatedResult.totalPages();

        links.add(createLink("self", baseUrl, page, size));
        links.add(createLink("first", baseUrl, 1, size));

        if (total > 0) {
            links.add(createLink("last", baseUrl, total, size));
        }
        if (page < total) {
            links.add(createLink("next", baseUrl, page + 1, size));
        }
        if (page > 1) {
            links.add(createLink("prev", baseUrl, page - 1, size));
        }

        response.setLinks(links);

        return response;
    }

    @Named("EnumToString")
    default String enumToString(BookGenre bookGenre) {
        return bookGenre.getDisplayName();
    }

    @Named("StringToEnum")
    default BookGenre stringToEnum(BookRequest.BookGenreEnum bookGenreEnum) {
        return BookGenre.valueOf(bookGenreEnum.getValue());
    }

    @Named("LocalDateTimeToOffsetDateTime")
    default OffsetDateTime getOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
    }

    private OffsetDateTime nowToUtcOffsetDateTime() {
        return OffsetDateTime.now().withNano(0).withOffsetSameInstant(ZoneOffset.UTC);
    }

    private Link createLink(String rel, String href, String method) {
        Link link = new Link();
        link.setRel(rel);
        link.setHref(href);
        link.setMethod(method);
        return link;
    }

    private Link createLink(String rel, String baseUrl, int page, int size) {
        String href = baseUrl + "?page=" + page + "&pageSize=" + size;
        return createLink(rel, href, "GET");
    }
}
