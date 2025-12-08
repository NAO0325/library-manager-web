package com.library.manager.driving.controllers.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.manager.application.exceptions.BookNotFoundException;
import com.library.manager.application.ports.driving.BookServicePort;
import com.library.manager.domain.Book;
import com.library.manager.domain.BookGenre;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.domain.valueobjects.PaginationQuery;
import com.library.manager.driving.controllers.config.TestConfiguration;
import com.library.manager.driving.controllers.error.CustomExceptionHandler;
import com.library.manager.driving.controllers.mappers.BookMapper;
import com.library.manager.driving.controllers.models.BookRequest;
import com.library.manager.driving.controllers.models.BooksResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookControllerAdapter.class)
@ContextConfiguration(classes = {TestConfiguration.class, BookControllerAdapter.class, CustomExceptionHandler.class})
@DisplayName("BookControllerAdapter Integration Tests")
class BookControllerAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookServicePort bookServicePort;

    @MockitoBean
    private BookMapper bookMapper;

    private Book testBook;
    private BookRequest testBookRequest;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setBookGenre(BookGenre.FICTION);
        testBook.setPages(300);
        testBook.setPublicationYear(2024);
        testBook.setActive(true);
        testBook.setCreatedAt(LocalDateTime.now());
        testBook.setUpdatedAt(LocalDateTime.now());

        testBookRequest = new BookRequest();
        testBookRequest.setTitle("Test Book");
        testBookRequest.setAuthor("Test Author");
        testBookRequest.setBookGenre(BookRequest.BookGenreEnum.FICTION);
        testBookRequest.setPages(300);
        testBookRequest.setPublicationYear(2024);
    }

    @Nested
    @DisplayName("POST /v1/books - createBook() endpoint tests")
    class CreateBookTests {

        @Test
        @DisplayName("Should create book and return 201 CREATED")
        void shouldCreateBookSuccessfully() throws Exception {
            // Arrange
            when(bookMapper.toBook(any(BookRequest.class))).thenReturn(testBook);
            when(bookServicePort.save(any(Book.class))).thenReturn(testBook);
            when(bookMapper.toBookResponse(any(Book.class))).thenReturn(any());

            // Act & Assert
            mockMvc.perform(post("/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testBookRequest)))
                    .andExpect(status().isCreated());

            verify(bookMapper, times(1)).toBook(any(BookRequest.class));
            verify(bookServicePort, times(1)).save(any(Book.class));
            verify(bookMapper, times(1)).toBookResponse(any(Book.class));
        }

        @Test
        @DisplayName("Should validate required fields")
        void shouldValidateRequiredFields() throws Exception {
            // Arrange
            BookRequest invalidRequest = new BookRequest();
            // Missing required fields

            // Act & Assert
            mockMvc.perform(post("/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(bookServicePort, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("Should handle invalid JSON")
        void shouldHandleInvalidJson() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid json}"))
                    .andExpect(status().isBadRequest());

            verify(bookServicePort, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("Should handle valid book with all fields")
        void shouldHandleValidBookWithAllFields() throws Exception {
            // Arrange
            when(bookMapper.toBook(any(BookRequest.class))).thenReturn(testBook);
            when(bookServicePort.save(any(Book.class))).thenReturn(testBook);
            when(bookMapper.toBookResponse(any(Book.class))).thenReturn(any());

            // Act & Assert
            mockMvc.perform(post("/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testBookRequest)))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("GET /v1/books - getBooks() endpoint tests")
    class GetBooksTests {

        @Test
        @DisplayName("Should return books with default pagination")
        void shouldReturnBooksWithDefaultPagination() throws Exception {
            // Arrange
            PaginatedResult<Book> result = new PaginatedResult<>(
                    List.of(testBook),
                    1L,
                    1,
                    1,
                    10
            );

            when(bookMapper.toFilter(any(), any(), any())).thenReturn(new BookFilter(null, null, null, true));
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(result);
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(mock(BooksResponse.class));

            // Act & Assert
            mockMvc.perform(get("/v1/books"))
                    .andExpect(status().isOk());

            verify(bookServicePort, times(1)).getAllWithFilters(
                    any(BookFilter.class),
                    argThat(query -> query.page() == 0 && query.pageSize() == 10)
            );
        }

        @Test
        @DisplayName("Should apply custom pagination parameters")
        void shouldApplyCustomPaginationParameters() throws Exception {
            // Arrange
            PaginatedResult<Book> result = new PaginatedResult<>(List.of(), 0L, 0, 0, 20);

            when(bookMapper.toFilter(any(), any(), any())).thenReturn(new BookFilter(null, null, null, true));
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(result);
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(mock(BooksResponse.class));

            // Act & Assert
            mockMvc.perform(get("/v1/books")
                            .param("page", "3")
                            .param("pageSize", "20"))
                    .andExpect(status().isOk());

            verify(bookServicePort, times(1)).getAllWithFilters(
                    any(BookFilter.class),
                    argThat(query -> query.page() == 2 && query.pageSize() == 20)
            );
        }

        @Test
        @DisplayName("Should apply sorting parameters")
        void shouldApplySortingParameters() throws Exception {
            // Arrange
            PaginatedResult<Book> result = new PaginatedResult<>(List.of(), 0L, 0, 0, 10);

            when(bookMapper.toFilter(any(), any(), any())).thenReturn(new BookFilter(null, null, null, true));
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(result);
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(mock(BooksResponse.class));

            // Act & Assert
            mockMvc.perform(get("/v1/books")
                            .param("sortBy", "title")
                            .param("sortDirection", "DESC"))
                    .andExpect(status().isOk());

            verify(bookServicePort, times(1)).getAllWithFilters(
                    any(BookFilter.class),
                    argThat(query -> query.sortBy().equals("title") && query.sortDirection().equals("DESC"))
            );
        }

        @Test
        @DisplayName("Should filter by author")
        void shouldFilterByAuthor() throws Exception {
            // Arrange
            PaginatedResult<Book> result = new PaginatedResult<>(List.of(testBook), 1L, 1, 1, 10);

            when(bookMapper.toFilter("Test Author", null, null))
                    .thenReturn(new BookFilter(null, "Test Author", null, true));
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(result);
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(mock(BooksResponse.class));

            // Act & Assert
            mockMvc.perform(get("/v1/books")
                            .param("author", "Test Author"))
                    .andExpect(status().isOk());

            verify(bookMapper, times(1)).toFilter("Test Author", null, null);
        }

        @Test
        @DisplayName("Should filter by title")
        void shouldFilterByTitle() throws Exception {
            // Arrange
            PaginatedResult<Book> result = new PaginatedResult<>(List.of(testBook), 1L, 1, 1, 10);

            when(bookMapper.toFilter(null, "Test Book", null))
                    .thenReturn(new BookFilter("Test Book", null, null, true));
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(result);
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(mock(BooksResponse.class));

            // Act & Assert
            mockMvc.perform(get("/v1/books")
                            .param("title", "Test Book"))
                    .andExpect(status().isOk());

            verify(bookMapper, times(1)).toFilter(null, "Test Book", null);
        }

        @Test
        @DisplayName("Should filter by genre (case insensitive)")
        void shouldFilterByGenre() throws Exception {
            // Arrange
            PaginatedResult<Book> result = new PaginatedResult<>(List.of(testBook), 1L, 1, 1, 10);

            when(bookMapper.toFilter(null, null, BookGenre.FICTION))
                    .thenReturn(new BookFilter(null, null, BookGenre.FICTION, true));
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(result);
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(mock(BooksResponse.class));

            // Act & Assert
            mockMvc.perform(get("/v1/books")
                            .param("genre", "fiction"))
                    .andExpect(status().isOk());

            verify(bookMapper, times(1)).toFilter(null, null, BookGenre.FICTION);
        }

        @Test
        @DisplayName("Should handle genre uppercase conversion")
        void shouldHandleGenreUppercaseConversion() throws Exception {
            // Arrange
            PaginatedResult<Book> result = new PaginatedResult<>(List.of(), 0L, 0, 0, 10);

            when(bookMapper.toFilter(null, null, BookGenre.SCIENCE_FICTION))
                    .thenReturn(new BookFilter(null, null, BookGenre.SCIENCE_FICTION, true));
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(result);
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(mock(BooksResponse.class));

            // Act & Assert
            mockMvc.perform(get("/v1/books")
                            .param("genre", "science_fiction"))
                    .andExpect(status().isOk());

            verify(bookMapper, times(1)).toFilter(null, null, BookGenre.SCIENCE_FICTION);
        }

        @Test
        @DisplayName("Should handle invalid genre gracefully")
        void shouldHandleInvalidGenre() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/v1/books")
                            .param("genre", "INVALID_GENRE"))
                    .andExpect(status().isBadRequest());

            verify(bookServicePort, never()).getAllWithFilters(any(), any());
        }

        @Test
        @DisplayName("Should combine multiple filters")
        void shouldCombineMultipleFilters() throws Exception {
            // Arrange
            PaginatedResult<Book> result = new PaginatedResult<>(List.of(testBook), 1L, 1, 1, 10);

            when(bookMapper.toFilter("Author", "Title", BookGenre.MYSTERY))
                    .thenReturn(new BookFilter("Title", "Author", BookGenre.MYSTERY, true));
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(result);
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(mock(BooksResponse.class));

            // Act & Assert
            mockMvc.perform(get("/v1/books")
                            .param("author", "Author")
                            .param("title", "Title")
                            .param("genre", "mystery"))
                    .andExpect(status().isOk());

            verify(bookMapper, times(1)).toFilter("Author", "Title", BookGenre.MYSTERY);
        }

        @Test
        @DisplayName("Should handle empty results")
        void shouldHandleEmptyResults() throws Exception {
            // Arrange
            PaginatedResult<Book> emptyResult = new PaginatedResult<>(List.of(), 0L, 0, 0, 10);

            when(bookMapper.toFilter(null, null, null))
                    .thenReturn(new BookFilter(null, null, null, true));
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(emptyResult);
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(mock(BooksResponse.class));

            // Act & Assert
            mockMvc.perform(get("/v1/books"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should use default sort parameters when not provided")
        void shouldUseDefaultSortParameters() throws Exception {
            // Arrange
            PaginatedResult<Book> result = new PaginatedResult<>(List.of(), 0L, 0, 0, 10);

            when(bookMapper.toFilter(null, null, null))
                    .thenReturn(new BookFilter(null, null, null, true));
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(result);
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(mock(BooksResponse.class));

            // Act & Assert
            mockMvc.perform(get("/v1/books"))
                    .andExpect(status().isOk());

            verify(bookServicePort, times(1)).getAllWithFilters(
                    any(BookFilter.class),
                    argThat(query -> query.sortBy().equals("title") && query.sortDirection().equals("ASC"))
            );
        }
    }

    @Nested
    @DisplayName("GET /v1/books/{id} - getBook() endpoint tests")
    class GetBookTests {

        @Test
        @DisplayName("Should return book by ID with 200 OK")
        void shouldReturnBookById() throws Exception {
            // Arrange
            Long bookId = 1L;
            when(bookServicePort.findActiveById(bookId)).thenReturn(testBook);
            when(bookMapper.toBookResponse(testBook)).thenReturn(mock(com.library.manager.driving.controllers.models.BookResponse.class));

            // Act & Assert
            mockMvc.perform(get("/v1/books/{id}", bookId))
                    .andExpect(status().isOk());

            verify(bookServicePort, times(1)).findActiveById(bookId);
            verify(bookMapper, times(1)).toBookResponse(testBook);
        }

        @Test
        @DisplayName("Should return 404 when book not found")
        void shouldReturn404WhenBookNotFound() throws Exception {
            // Arrange
            Long bookId = 999L;
            when(bookServicePort.findActiveById(bookId)).thenThrow(new BookNotFoundException(bookId));

            // Act & Assert
            mockMvc.perform(get("/v1/books/{id}", bookId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("NOT_FOUND"));

            verify(bookServicePort, times(1)).findActiveById(bookId);
        }

        @Test
        @DisplayName("Should handle various valid ID formats")
        void shouldHandleVariousIdFormats() throws Exception {
            // Arrange
            Long bookId = 123456789L;
            when(bookServicePort.findActiveById(bookId)).thenReturn(testBook);
            when(bookMapper.toBookResponse(testBook)).thenReturn(mock(com.library.manager.driving.controllers.models.BookResponse.class));

            // Act & Assert
            mockMvc.perform(get("/v1/books/{id}", bookId))
                    .andExpect(status().isOk());

            verify(bookServicePort, times(1)).findActiveById(bookId);
        }

        @Test
        @DisplayName("Should return proper book response structure")
        void shouldReturnProperResponseStructure() throws Exception {
            // Arrange
            Long bookId = 1L;
            BookResponse mockResponse = new BookResponse();
            mockResponse.setId(bookId);
            mockResponse.setTitle("Test Book");
            mockResponse.setAuthor("Test Author");

            when(bookServicePort.findActiveById(bookId)).thenReturn(testBook);
            when(bookMapper.toBookResponse(testBook)).thenReturn(mockResponse);

            // Act & Assert
            mockMvc.perform(get("/v1/books/{id}", bookId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(bookId))
                    .andExpect(jsonPath("$.title").value("Test Book"))
                    .andExpect(jsonPath("$.author").value("Test Author"));
        }
    }

    @Nested
    @DisplayName("DELETE /v1/books/{id} - deactivateBook() endpoint tests")
    class DeactivateBookTests {

        @Test
        @DisplayName("Should deactivate book and return 204 NO CONTENT")
        void shouldDeactivateBookSuccessfully() throws Exception {
            // Arrange
            Long bookId = 1L;
            doNothing().when(bookServicePort).deactivate(bookId);

            // Act & Assert
            mockMvc.perform(delete("/v1/books/{id}", bookId))
                    .andExpect(status().isNoContent());

            verify(bookServicePort, times(1)).deactivate(bookId);
        }

        @Test
        @DisplayName("Should return 404 when trying to deactivate non-existent book")
        void shouldReturn404WhenDeactivatingNonExistentBook() throws Exception {
            // Arrange
            Long bookId = 999L;
            doThrow(new BookNotFoundException(bookId)).when(bookServicePort).deactivate(bookId);

            // Act & Assert
            mockMvc.perform(delete("/v1/books/{id}", bookId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("NOT_FOUND"));

            verify(bookServicePort, times(1)).deactivate(bookId);
        }

        @Test
        @DisplayName("Should handle deactivation of different book IDs")
        void shouldHandleDifferentBookIds() throws Exception {
            // Arrange
            Long bookId = 12345L;
            doNothing().when(bookServicePort).deactivate(bookId);

            // Act & Assert
            mockMvc.perform(delete("/v1/books/{id}", bookId))
                    .andExpect(status().isNoContent());

            verify(bookServicePort, times(1)).deactivate(bookId);
        }

        @Test
        @DisplayName("Should handle service layer exceptions during deactivation")
        void shouldHandleServiceExceptions() throws Exception {
            // Arrange
            Long bookId = 1L;
            doThrow(new IllegalArgumentException("Book cannot be deactivated"))
                    .when(bookServicePort).deactivate(bookId);

            // Act & Assert
            mockMvc.perform(delete("/v1/books/{id}", bookId))
                    .andExpect(status().isBadRequest());

            verify(bookServicePort, times(1)).deactivate(bookId);
        }
    }

    @Nested
    @DisplayName("PUT /v1/books/{id} - updateBook() endpoint tests")
    class UpdateBookTests {

        @Test
        @DisplayName("Should update book successfully and return 200 OK")
        void shouldUpdateBookSuccessfully() throws Exception {
            // Arrange
            Long bookId = 1L;
            Book updatedBook = new Book();
            updatedBook.setId(bookId);
            updatedBook.setTitle(testBookRequest.getTitle());
            updatedBook.setAuthor(testBookRequest.getAuthor());

            when(bookMapper.toBook(any(BookRequest.class))).thenReturn(testBook);
            when(bookServicePort.update(any(Book.class))).thenReturn(updatedBook);
            when(bookMapper.toBookResponse(any(Book.class))).thenReturn(mock(BookResponse.class));

            // Act & Assert
            mockMvc.perform(put("/v1/books/{id}", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testBookRequest)))
                    .andExpect(status().isOk());

            verify(bookMapper, times(1)).toBook(testBookRequest);
            verify(bookServicePort, times(1)).update(argThat(book -> book.getId().equals(bookId)));
            verify(bookMapper, times(1)).toBookResponse(updatedBook);
        }

        @Test
        @DisplayName("Should return 404 when updating non-existent book")
        void shouldReturn404WhenBookNotFound() throws Exception {
            // Arrange
            Long bookId = 999L;
            when(bookMapper.toBook(any(BookRequest.class))).thenReturn(testBook);
            when(bookServicePort.update(any(Book.class))).thenThrow(new BookNotFoundException(bookId));

            // Act & Assert
            mockMvc.perform(put("/v1/books/{id}", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testBookRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("NOT_FOUND"));

            verify(bookServicePort, times(1)).update(any(Book.class));
        }

        @Test
        @DisplayName("Should validate request body structure")
        void shouldValidateRequestBody() throws Exception {
            // Arrange
            BookRequest invalidRequest = new BookRequest();
            // Missing required fields

            // Act & Assert
            mockMvc.perform(put("/v1/books/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(bookServicePort, never()).update(any(Book.class));
        }

        @Test
        @DisplayName("Should handle invalid JSON in request body")
        void shouldHandleInvalidJson() throws Exception {
            // Act & Assert
            mockMvc.perform(put("/v1/books/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid json}"))
                    .andExpect(status().isBadRequest());

            verify(bookServicePort, never()).update(any(Book.class));
        }

        @Test
        @DisplayName("Should update book with different ID values")
        void shouldUpdateBookWithDifferentIds() throws Exception {
            // Arrange
            Long bookId = 12345L;
            Book updatedBook = new Book();
            updatedBook.setId(bookId);

            when(bookMapper.toBook(any(BookRequest.class))).thenReturn(testBook);
            when(bookServicePort.update(any(Book.class))).thenReturn(updatedBook);
            when(bookMapper.toBookResponse(any(Book.class))).thenReturn(mock(BookResponse.class));

            // Act & Assert
            mockMvc.perform(put("/v1/books/{id}", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testBookRequest)))
                    .andExpect(status().isOk());

            verify(bookServicePort, times(1)).update(argThat(book -> book.getId().equals(bookId)));
        }

        @Test
        @DisplayName("Should properly set ID on book before updating")
        void shouldSetIdBeforeUpdate() throws Exception {
            // Arrange
            Long bookId = 42L;
            Book bookToUpdate = new Book();
            bookToUpdate.setTitle("Updated Title");

            when(bookMapper.toBook(any(BookRequest.class))).thenReturn(bookToUpdate);
            when(bookServicePort.update(any(Book.class))).thenReturn(bookToUpdate);
            when(bookMapper.toBookResponse(any(Book.class))).thenReturn(mock(BookResponse.class));

            // Act
            mockMvc.perform(put("/v1/books/{id}", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testBookRequest)))
                    .andExpect(status().isOk());

            // Assert - verify the book passed to update has the correct ID
            verify(bookServicePort, times(1)).update(argThat(book ->
                    book.getId() != null && book.getId().equals(bookId)
            ));
        }

        @Test
        @DisplayName("Should return updated book response with all fields")
        void shouldReturnCompleteUpdatedBookResponse() throws Exception {
            // Arrange
            Long bookId = 1L;
            Book updatedBook = new Book();
            updatedBook.setId(bookId);
            updatedBook.setTitle("Updated Title");
            updatedBook.setAuthor("Updated Author");

            BookResponse mockResponse = new BookResponse();
            mockResponse.setId(bookId);
            mockResponse.setTitle("Updated Title");
            mockResponse.setAuthor("Updated Author");

            when(bookMapper.toBook(any(BookRequest.class))).thenReturn(testBook);
            when(bookServicePort.update(any(Book.class))).thenReturn(updatedBook);
            when(bookMapper.toBookResponse(updatedBook)).thenReturn(mockResponse);

            // Act & Assert
            mockMvc.perform(put("/v1/books/{id}", bookId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testBookRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(bookId))
                    .andExpect(jsonPath("$.title").value("Updated Title"))
                    .andExpect(jsonPath("$.author").value("Updated Author"));
        }
    }

    @Nested
    @DisplayName("Exception handling tests")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("Should handle BookNotFoundException and return 404")
        void shouldHandleBookNotFoundException() throws Exception {
            // Arrange
            when(bookMapper.toBook(any(BookRequest.class))).thenReturn(testBook);
            when(bookServicePort.save(any(Book.class))).thenThrow(new BookNotFoundException(1L));

            // Act & Assert
            mockMvc.perform(post("/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testBookRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should handle IllegalArgumentException and return 400")
        void shouldHandleIllegalArgumentException() throws Exception {
            // Arrange
            when(bookMapper.toBook(any(BookRequest.class))).thenReturn(testBook);
            when(bookServicePort.save(any(Book.class)))
                    .thenThrow(new IllegalArgumentException("Invalid criteria"));

            // Act & Assert
            mockMvc.perform(post("/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testBookRequest)))
                    .andExpect(status().isBadRequest());
        }
    }
}
