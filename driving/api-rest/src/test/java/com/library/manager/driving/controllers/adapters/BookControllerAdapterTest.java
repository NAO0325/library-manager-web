package com.library.manager.driving.controllers.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.manager.application.exceptions.BookNotFoundException;
import com.library.manager.application.ports.driving.BookServicePort;
import com.library.manager.domain.Book;
import com.library.manager.domain.BookGenre;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.domain.valueobjects.PaginationQuery;
import com.library.manager.driving.controllers.mappers.BookMapper;
import com.library.manager.driving.controllers.models.BookRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
@DisplayName("BookControllerAdapter Integration Tests")
class BookControllerAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookServicePort bookServicePort;

    @MockBean
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

            when(bookMapper.toFilter(null, null, null)).thenReturn(new BookFilter(null, null, null, true));
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(result);
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(any());

            // Act & Assert
            mockMvc.perform(get("/v1/books"))
                    .andExpect(status().isOk());

            verify(bookServicePort, times(1)).getAllWithFilters(
                    any(BookFilter.class),
                    argThat(query -> query.pageNumber() == 0 && query.pageSize() == 10)
            );
        }

        @Test
        @DisplayName("Should apply custom pagination parameters")
        void shouldApplyCustomPaginationParameters() throws Exception {
            // Arrange
            PaginatedResult<Book> result = new PaginatedResult<>(List.of(), 0L, 0, 0, 20);

            when(bookMapper.toFilter(null, null, null)).thenReturn(new BookFilter(null, null, null, true));
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(result);
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(any());

            // Act & Assert
            mockMvc.perform(get("/v1/books")
                            .param("page", "3")
                            .param("pageSize", "20"))
                    .andExpect(status().isOk());

            verify(bookServicePort, times(1)).getAllWithFilters(
                    any(BookFilter.class),
                    argThat(query -> query.pageNumber() == 2 && query.pageSize() == 20)
            );
        }

        @Test
        @DisplayName("Should apply sorting parameters")
        void shouldApplySortingParameters() throws Exception {
            // Arrange
            PaginatedResult<Book> result = new PaginatedResult<>(List.of(), 0L, 0, 0, 10);

            when(bookMapper.toFilter(null, null, null)).thenReturn(new BookFilter(null, null, null, true));
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(result);
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(any());

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
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(any());

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
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(any());

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
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(any());

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
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(any());

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
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(any());

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
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(any());

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
            when(bookMapper.toResponse(any(PaginatedResult.class))).thenReturn(any());

            // Act & Assert
            mockMvc.perform(get("/v1/books"))
                    .andExpect(status().isOk());

            verify(bookServicePort, times(1)).getAllWithFilters(
                    any(BookFilter.class),
                    argThat(query -> query.sortBy().equals("id") && query.sortDirection().equals("ASC"))
            );
        }
    }

    @Nested
    @DisplayName("PUT /v1/books/{id} - updateBook() endpoint tests")
    class UpdateBookTests {

        @Test
        @DisplayName("Should return null (not implemented yet)")
        void shouldReturnNullForUpdateEndpoint() throws Exception {
            // Act & Assert
            mockMvc.perform(put("/v1/books/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testBookRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").doesNotExist());

            verify(bookServicePort, never()).update(any(Book.class));
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
