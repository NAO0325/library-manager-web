package com.library.manager.driving.controllers.adapters;


import com.library.manager.application.ports.driving.BookServicePort;
import com.library.manager.domain.Book;
import com.library.manager.domain.BookGenre;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.domain.valueobjects.PaginationQuery;
import com.library.manager.driving.controllers.api.BooksApi;
import com.library.manager.driving.controllers.mappers.BookMapper;
import com.library.manager.driving.controllers.models.BookRequest;
import com.library.manager.driving.controllers.models.BookResponse;
import com.library.manager.driving.controllers.models.BooksResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookControllerAdapter implements BooksApi {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_SORT_BY = "id";
    private static final String DEFAULT_SORT_DIRECTION = "ASC";

    private final BookServicePort bookServicePort;

    private final BookMapper mapper;

    @Override
    public ResponseEntity<BookResponse> createBook(BookRequest bookRequest) {

        Book book = bookServicePort.save(mapper.toBook(bookRequest));

        BookResponse response = mapper.toBookResponse(book);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<BookResponse> getBook(Long id) {

        BookResponse response = mapper.toBookResponse(bookServicePort.findActiveById(id));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deactivateBook(Long id) {

        bookServicePort.deactivate(id);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<BookResponse> updateBook(Long id, BookRequest bookRequest) {

        Book book = mapper.toBook(bookRequest);
        book.setId(id);

        BookResponse response = mapper.toBookResponse(bookServicePort.update(book));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BooksResponse> getBooks(Optional<Integer> page,
                                                  Optional<Integer> pageSize,
                                                  Optional<String> sortDirection,
                                                  Optional<String> sortBy,
                                                  Optional<String> author,
                                                  Optional<String> title,
                                                  Optional<String> genre) {

        String genreFilter = genre.map(String::toUpperCase).orElse(null);
        BookGenre bookGenre = (genreFilter != null) ? BookGenre.valueOf(genreFilter) : null;
        BookFilter filter = mapper.toFilter(author.orElse(null), title.orElse(null), bookGenre);

        PaginatedResult<Book> paginatedResult = bookServicePort.getAllWithFilters(
                filter,
                new PaginationQuery(
                        page.orElse(DEFAULT_PAGE) - 1,
                        pageSize.orElse(DEFAULT_PAGE_SIZE),
                        sortBy.orElse(DEFAULT_SORT_BY),
                        sortDirection.orElse(DEFAULT_SORT_DIRECTION)
                ));

        BooksResponse response = mapper.toResponse(paginatedResult);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
