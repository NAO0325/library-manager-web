package com.library.manager.driving.controllers.adapters;


import com.library.manager.application.ports.driving.BookServicePort;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginationQuery;
import com.library.manager.driving.controllers.api.BooksApi;
import com.library.manager.driving.controllers.models.BooksResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookControllerAdapter implements BooksApi {

    private final BookServicePort bookServicePort;

    @Override
    public ResponseEntity<BooksResponse> getBooks(Optional<@Min(1) Integer> page,
                                                  Optional<@Min(1) @Max(100) Integer> pageSize,
                                                  Optional<String> sortDirection,
                                                  Optional<String> sortBy,
                                                  Optional<@Size(max = 250) String> author,
                                                  Optional<@Size(max = 250) String> title,
                                                  Optional<String> genre) {

        BookFilter bf = new BookFilter(null, null, null, true);

        bookServicePort.getAllWithFilters(bf, new PaginationQuery(1, 10));

        return null;
    }
}
