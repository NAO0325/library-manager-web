package com.library.manager.driving.web.controllers;

import com.library.manager.application.exceptions.BookNotFoundException;
import com.library.manager.application.ports.driving.BookServicePort;
import com.library.manager.domain.Book;
import com.library.manager.domain.BookGenre;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.domain.valueobjects.PaginationQuery;
import com.library.manager.driving.web.config.TestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
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

@WebMvcTest(BookWebController.class)
@ContextConfiguration(classes = {TestConfiguration.class, BookWebController.class,
        com.library.manager.driving.web.exception.WebExceptionHandler.class})
@DisplayName("BookWebController MVC Tests")
class BookWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookServicePort bookServicePort;

    private Book testBook;
    private PaginatedResult<Book> paginatedResult;

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

        paginatedResult = new PaginatedResult<>(
                List.of(testBook),
                1L,
                1,
                0,
                10
        );
    }

    @Nested
    @DisplayName("GET /ui/books - List books tests")
    class ListBooksTests {

        @Test
        @DisplayName("Should display books list with default pagination")
        void shouldDisplayBooksListWithDefaults() throws Exception {
            // Arrange
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(paginatedResult);

            // Act & Assert
            mockMvc.perform(get("/ui/books"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/list"))
                    .andExpect(model().attributeExists("page"))
                    .andExpect(model().attributeExists("filter"))
                    .andExpect(model().attributeExists("genres"))
                    .andExpect(model().attribute("sortBy", "id"))
                    .andExpect(model().attribute("sortDir", "asc"))
                    .andExpect(model().attribute("reverseSortDir", "desc"));

            verify(bookServicePort, times(1)).getAllWithFilters(
                    any(BookFilter.class),
                    argThat(query -> query.page() == 0 && query.pageSize() == 10)
            );
        }

        @Test
        @DisplayName("Should display books list with custom pagination parameters")
        void shouldDisplayBooksListWithCustomPagination() throws Exception {
            // Arrange
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(paginatedResult);

            // Act & Assert
            mockMvc.perform(get("/ui/books")
                            .param("page", "2")
                            .param("size", "20")
                            .param("sortBy", "title")
                            .param("sortDir", "desc"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/list"))
                    .andExpect(model().attribute("sortBy", "title"))
                    .andExpect(model().attribute("sortDir", "desc"))
                    .andExpect(model().attribute("reverseSortDir", "asc"));

            verify(bookServicePort, times(1)).getAllWithFilters(
                    any(BookFilter.class),
                    argThat(query -> query.page() == 2 && query.pageSize() == 20
                            && query.sortBy().equals("title") && query.sortDirection().equals("desc"))
            );
        }

        @Test
        @DisplayName("Should filter books by title")
        void shouldFilterBooksByTitle() throws Exception {
            // Arrange
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(paginatedResult);

            // Act & Assert
            mockMvc.perform(get("/ui/books")
                            .param("title", "Test Book"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/list"));

            verify(bookServicePort, times(1)).getAllWithFilters(
                    argThat(filter -> filter.title() != null && filter.title().equals("Test Book")),
                    any(PaginationQuery.class)
            );
        }

        @Test
        @DisplayName("Should filter books by author")
        void shouldFilterBooksByAuthor() throws Exception {
            // Arrange
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(paginatedResult);

            // Act & Assert
            mockMvc.perform(get("/ui/books")
                            .param("author", "Test Author"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/list"));

            verify(bookServicePort, times(1)).getAllWithFilters(
                    argThat(filter -> filter.author() != null && filter.author().equals("Test Author")),
                    any(PaginationQuery.class)
            );
        }

        @Test
        @DisplayName("Should filter books by genre")
        void shouldFilterBooksByGenre() throws Exception {
            // Arrange
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(paginatedResult);

            // Act & Assert
            mockMvc.perform(get("/ui/books")
                            .param("genre", "FICTION"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/list"));

            verify(bookServicePort, times(1)).getAllWithFilters(
                    argThat(filter -> filter.bookGenre() == BookGenre.FICTION),
                    any(PaginationQuery.class)
            );
        }

        @Test
        @DisplayName("Should filter books by active status")
        void shouldFilterBooksByActiveStatus() throws Exception {
            // Arrange
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(paginatedResult);

            // Act & Assert
            mockMvc.perform(get("/ui/books")
                            .param("active", "true"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/list"));

            verify(bookServicePort, times(1)).getAllWithFilters(
                    argThat(filter -> filter.active() != null && filter.active()),
                    any(PaginationQuery.class)
            );
        }

        @Test
        @DisplayName("Should combine multiple filters")
        void shouldCombineMultipleFilters() throws Exception {
            // Arrange
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(paginatedResult);

            // Act & Assert
            mockMvc.perform(get("/ui/books")
                            .param("title", "Test")
                            .param("author", "Author")
                            .param("genre", "FICTION")
                            .param("active", "true"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/list"));

            verify(bookServicePort, times(1)).getAllWithFilters(
                    argThat(filter ->
                            filter.title().equals("Test") &&
                                    filter.author().equals("Author") &&
                                    filter.bookGenre() == BookGenre.FICTION &&
                                    filter.active()
                    ),
                    any(PaginationQuery.class)
            );
        }

        @Test
        @DisplayName("Should include all genres in model")
        void shouldIncludeAllGenresInModel() throws Exception {
            // Arrange
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(paginatedResult);

            // Act & Assert
            mockMvc.perform(get("/ui/books"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("genres", BookGenre.values()));
        }
    }

    @Nested
    @DisplayName("GET /ui/books/new - Create form tests")
    class CreateFormTests {

        @Test
        @DisplayName("Should display create form with new book")
        void shouldDisplayCreateForm() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/ui/books/new"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/create"))
                    .andExpect(model().attributeExists("book"))
                    .andExpect(model().attributeExists("genres"))
                    .andExpect(model().attribute("genres", BookGenre.values()));

            verify(bookServicePort, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("Should include empty book object in model")
        void shouldIncludeEmptyBookInModel() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/ui/books/new"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("book", instanceOf(Book.class)));
        }
    }

    @Nested
    @DisplayName("POST /ui/books - Save book tests")
    class SaveBookTests {

        @Test
        @DisplayName("Should save new book and redirect")
        void shouldSaveNewBook() throws Exception {
            // Arrange
            when(bookServicePort.save(any(Book.class))).thenReturn(testBook);

            // Act & Assert
            mockMvc.perform(post("/ui/books")
                            .param("title", "New Book")
                            .param("author", "New Author")
                            .param("bookGenre", "FICTION")
                            .param("pages", "250")
                            .param("publicationYear", "2024"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/ui/books?successMessage=Book created successfully"));

            verify(bookServicePort, times(1)).save(argThat(book ->
                    book.getTitle().equals("New Book") &&
                            book.getAuthor().equals("New Author") &&
                            book.getBookGenre() == BookGenre.FICTION
            ));
        }

        @Test
        @DisplayName("Should save book with all fields")
        void shouldSaveBookWithAllFields() throws Exception {
            // Arrange
            when(bookServicePort.save(any(Book.class))).thenReturn(testBook);

            // Act & Assert
            mockMvc.perform(post("/ui/books")
                            .param("title", "Complete Book")
                            .param("author", "Complete Author")
                            .param("bookGenre", "SCIENCE_FICTION")
                            .param("pages", "500")
                            .param("publicationYear", "2023"))
                    .andExpect(status().is3xxRedirection());

            verify(bookServicePort, times(1)).save(any(Book.class));
        }
    }

    @Nested
    @DisplayName("GET /ui/books/{id} - Detail view tests")
    class DetailTests {

        @Test
        @DisplayName("Should display book detail")
        void shouldDisplayBookDetail() throws Exception {
            // Arrange
            when(bookServicePort.findActiveById(1L)).thenReturn(testBook);

            // Act & Assert
            mockMvc.perform(get("/ui/books/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/detail"))
                    .andExpect(model().attributeExists("book"))
                    .andExpect(model().attribute("book", testBook));

            verify(bookServicePort, times(1)).findActiveById(1L);
        }

        @Test
        @DisplayName("Should handle book not found exception")
        void shouldHandleBookNotFound() throws Exception {
            // Arrange
            when(bookServicePort.findActiveById(999L)).thenThrow(new BookNotFoundException(999L));

            // Act & Assert
            mockMvc.perform(get("/ui/books/{id}", 999L))
                    .andExpect(status().isNotFound());

            verify(bookServicePort, times(1)).findActiveById(999L);
        }

        @Test
        @DisplayName("Should display detail for different book IDs")
        void shouldDisplayDetailForDifferentIds() throws Exception {
            // Arrange
            Book anotherBook = new Book();
            anotherBook.setId(42L);
            anotherBook.setTitle("Another Book");
            anotherBook.setAuthor("Another Author");
            anotherBook.setBookGenre(BookGenre.MYSTERY);
            anotherBook.setPages(200);
            anotherBook.setPublicationYear(2023);
            anotherBook.setActive(true);
            when(bookServicePort.findActiveById(42L)).thenReturn(anotherBook);

            // Act & Assert
            mockMvc.perform(get("/ui/books/{id}", 42L))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("book", anotherBook));
        }
    }

    @Nested
    @DisplayName("GET /ui/books/{id}/edit - Edit form tests")
    class EditFormTests {

        @Test
        @DisplayName("Should display edit form with book data")
        void shouldDisplayEditForm() throws Exception {
            // Arrange
            when(bookServicePort.findActiveById(1L)).thenReturn(testBook);

            // Act & Assert
            mockMvc.perform(get("/ui/books/{id}/edit", 1L))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/edit"))
                    .andExpect(model().attributeExists("book"))
                    .andExpect(model().attributeExists("genres"))
                    .andExpect(model().attribute("book", testBook))
                    .andExpect(model().attribute("genres", BookGenre.values()));

            verify(bookServicePort, times(1)).findActiveById(1L);
        }

        @Test
        @DisplayName("Should handle non-existent book in edit form")
        void shouldHandleNonExistentBookInEditForm() throws Exception {
            // Arrange
            when(bookServicePort.findActiveById(999L)).thenThrow(new BookNotFoundException(999L));

            // Act & Assert
            mockMvc.perform(get("/ui/books/{id}/edit", 999L))
                    .andExpect(status().isNotFound());

            verify(bookServicePort, times(1)).findActiveById(999L);
        }
    }

    @Nested
    @DisplayName("POST /ui/books/{id} - Update book tests")
    class UpdateBookTests {

        @Test
        @DisplayName("Should update book and redirect")
        void shouldUpdateBook() throws Exception {
            // Arrange
            when(bookServicePort.update(any(Book.class))).thenReturn(testBook);

            // Act & Assert
            mockMvc.perform(post("/ui/books/{id}", 1L)
                            .param("title", "Updated Book")
                            .param("author", "Updated Author")
                            .param("bookGenre", "MYSTERY")
                            .param("pages", "350")
                            .param("publicationYear", "2023"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/ui/books?successMessage=Book updated successfully"));

            verify(bookServicePort, times(1)).update(argThat(book ->
                    book.getId().equals(1L) &&
                            book.getTitle().equals("Updated Book") &&
                            book.getAuthor().equals("Updated Author")
            ));
        }

        @Test
        @DisplayName("Should set book ID before updating")
        void shouldSetBookIdBeforeUpdate() throws Exception {
            // Arrange
            when(bookServicePort.update(any(Book.class))).thenReturn(testBook);

            // Act & Assert
            mockMvc.perform(post("/ui/books/{id}", 42L)
                            .param("title", "Test")
                            .param("author", "Author")
                            .param("bookGenre", "FICTION")
                            .param("pages", "100")
                            .param("publicationYear", "2024"))
                    .andExpect(status().is3xxRedirection());

            verify(bookServicePort, times(1)).update(argThat(book ->
                    book.getId() != null && book.getId().equals(42L)
            ));
        }

        @Test
        @DisplayName("Should handle update of non-existent book")
        void shouldHandleUpdateNonExistentBook() throws Exception {
            // Arrange
            when(bookServicePort.update(any(Book.class))).thenThrow(new BookNotFoundException(999L));

            // Act & Assert
            mockMvc.perform(post("/ui/books/{id}", 999L)
                            .param("title", "Test")
                            .param("author", "Author")
                            .param("bookGenre", "FICTION")
                            .param("pages", "100")
                            .param("publicationYear", "2024"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /ui/books/{id}/delete - Delete book tests")
    class DeleteBookTests {

        @Test
        @DisplayName("Should deactivate book and redirect")
        void shouldDeactivateBook() throws Exception {
            // Arrange
            doNothing().when(bookServicePort).deactivate(1L);

            // Act & Assert
            mockMvc.perform(post("/ui/books/{id}/delete", 1L))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/ui/books?successMessage=Book deactivated successfully"));

            verify(bookServicePort, times(1)).deactivate(1L);
        }

        @Test
        @DisplayName("Should deactivate different book IDs")
        void shouldDeactivateDifferentIds() throws Exception {
            // Arrange
            doNothing().when(bookServicePort).deactivate(42L);

            // Act & Assert
            mockMvc.perform(post("/ui/books/{id}/delete", 42L))
                    .andExpect(status().is3xxRedirection());

            verify(bookServicePort, times(1)).deactivate(42L);
        }

        @Test
        @DisplayName("Should handle deactivation of non-existent book")
        void shouldHandleDeactivateNonExistentBook() throws Exception {
            // Arrange
            doThrow(new BookNotFoundException(999L)).when(bookServicePort).deactivate(999L);

            // Act & Assert
            mockMvc.perform(post("/ui/books/{id}/delete", 999L))
                    .andExpect(status().isNotFound());

            verify(bookServicePort, times(1)).deactivate(999L);
        }
    }

    @Nested
    @DisplayName("Integration tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle complete book lifecycle")
        void shouldHandleCompleteLifecycle() throws Exception {
            // 1. Display list
            when(bookServicePort.getAllWithFilters(any(BookFilter.class), any(PaginationQuery.class)))
                    .thenReturn(paginatedResult);
            mockMvc.perform(get("/ui/books"))
                    .andExpect(status().isOk());

            // 2. Show create form
            mockMvc.perform(get("/ui/books/new"))
                    .andExpect(status().isOk());

            // 3. Save new book
            when(bookServicePort.save(any(Book.class))).thenReturn(testBook);
            mockMvc.perform(post("/ui/books")
                            .param("title", "New Book")
                            .param("author", "New Author")
                            .param("bookGenre", "FICTION")
                            .param("pages", "250")
                            .param("publicationYear", "2024"))
                    .andExpect(status().is3xxRedirection());

            // 4. View detail
            when(bookServicePort.findActiveById(1L)).thenReturn(testBook);
            mockMvc.perform(get("/ui/books/{id}", 1L))
                    .andExpect(status().isOk());

            // 5. Show edit form
            mockMvc.perform(get("/ui/books/{id}/edit", 1L))
                    .andExpect(status().isOk());

            // 6. Update book
            when(bookServicePort.update(any(Book.class))).thenReturn(testBook);
            mockMvc.perform(post("/ui/books/{id}", 1L)
                            .param("title", "Updated")
                            .param("author", "Author")
                            .param("bookGenre", "FICTION")
                            .param("pages", "300")
                            .param("publicationYear", "2024"))
                    .andExpect(status().is3xxRedirection());

            // 7. Delete book
            doNothing().when(bookServicePort).deactivate(1L);
            mockMvc.perform(post("/ui/books/{id}/delete", 1L))
                    .andExpect(status().is3xxRedirection());

            // Verify all interactions
            verify(bookServicePort, times(1)).save(any(Book.class));
            verify(bookServicePort, times(2)).findActiveById(1L);
            verify(bookServicePort, times(1)).update(any(Book.class));
            verify(bookServicePort, times(1)).deactivate(1L);
        }
    }
}