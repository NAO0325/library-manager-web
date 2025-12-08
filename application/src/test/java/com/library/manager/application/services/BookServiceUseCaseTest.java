package com.library.manager.application.services;

import com.library.manager.application.exceptions.BookNotFoundException;
import com.library.manager.application.ports.driven.BookRepositoryPort;
import com.library.manager.domain.Book;
import com.library.manager.domain.BookGenre;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.domain.valueobjects.PaginationQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookServiceUseCase Unit Tests")
class BookServiceUseCaseTest {

    @Mock
    private BookRepositoryPort bookRepositoryPort;

    @InjectMocks
    private BookServiceUseCase bookServiceUseCase;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setBookGenre(BookGenre.FICTION);
        testBook.setActive(true);
        testBook.setCreatedAt(LocalDateTime.now());
        testBook.setUpdatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("save() method tests")
    class SaveTests {

        @Test
        @DisplayName("Should save book successfully and set active flag to true")
        void shouldSaveBookSuccessfully() {
            // Arrange
            Book newBook = new Book();
            newBook.setTitle("New Book");
            newBook.setAuthor("New Author");
            newBook.setBookGenre(BookGenre.SCIENCE_FICTION);

            Book savedBook = new Book();
            savedBook.setId(1L);
            savedBook.setTitle("New Book");
            savedBook.setAuthor("New Author");
            savedBook.setBookGenre(BookGenre.SCIENCE_FICTION);
            savedBook.setActive(true);
            savedBook.setCreatedAt(LocalDateTime.now());
            savedBook.setUpdatedAt(LocalDateTime.now());

            when(bookRepositoryPort.save(any(Book.class))).thenReturn(savedBook);

            // Act
            Book result = bookServiceUseCase.save(newBook);

            // Assert
            assertNotNull(result);
            assertTrue(newBook.getActive(), "Active flag should be set to true");
            assertNotNull(newBook.getCreatedAt(), "CreatedAt should be set");
            assertNotNull(newBook.getUpdatedAt(), "UpdatedAt should be set");
            verify(bookRepositoryPort, times(1)).save(newBook);
        }

        @Test
        @DisplayName("Should set createdAt and updatedAt timestamps")
        void shouldSetTimestamps() {
            // Arrange
            Book newBook = new Book();
            newBook.setTitle("New Book");
            newBook.setAuthor("New Author");
            newBook.setBookGenre(BookGenre.MYSTERY);

            when(bookRepositoryPort.save(any(Book.class))).thenReturn(newBook);

            LocalDateTime beforeSave = LocalDateTime.now().minusSeconds(1);

            // Act
            bookServiceUseCase.save(newBook);

            LocalDateTime afterSave = LocalDateTime.now().plusSeconds(1);

            // Assert
            assertNotNull(newBook.getCreatedAt());
            assertNotNull(newBook.getUpdatedAt());
            assertTrue(newBook.getCreatedAt().isAfter(beforeSave));
            assertTrue(newBook.getCreatedAt().isBefore(afterSave));
            assertEquals(newBook.getCreatedAt(), newBook.getUpdatedAt());
        }

        @Test
        @DisplayName("Should call repository save method exactly once")
        void shouldCallRepositorySaveOnce() {
            // Arrange
            Book newBook = new Book();
            newBook.setTitle("Test");
            newBook.setAuthor("Author");
            newBook.setBookGenre(BookGenre.FANTASY);

            when(bookRepositoryPort.save(any(Book.class))).thenReturn(newBook);

            // Act
            bookServiceUseCase.save(newBook);

            // Assert
            verify(bookRepositoryPort, times(1)).save(newBook);
            verifyNoMoreInteractions(bookRepositoryPort);
        }
    }

    @Nested
    @DisplayName("findActiveById() method tests")
    class FindActiveByIdTests {

        @Test
        @DisplayName("Should return active book when found")
        void shouldReturnActiveBookWhenFound() {
            // Arrange
            Long bookId = 1L;
            when(bookRepositoryPort.findActiveById(bookId)).thenReturn(Optional.of(testBook));

            // Act
            Book result = bookServiceUseCase.findActiveById(bookId);

            // Assert
            assertNotNull(result);
            assertEquals(testBook.getId(), result.getId());
            assertEquals(testBook.getTitle(), result.getTitle());
            verify(bookRepositoryPort, times(1)).findActiveById(bookId);
        }

        @Test
        @DisplayName("Should throw BookNotFoundException when book not found")
        void shouldThrowExceptionWhenBookNotFound() {
            // Arrange
            Long bookId = 999L;
            when(bookRepositoryPort.findActiveById(bookId)).thenReturn(Optional.empty());

            // Act & Assert
            BookNotFoundException exception = assertThrows(
                    BookNotFoundException.class,
                    () -> bookServiceUseCase.findActiveById(bookId)
            );

            assertTrue(exception.getMessage().contains("999"));
            verify(bookRepositoryPort, times(1)).findActiveById(bookId);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is null")
        void shouldThrowExceptionWhenIdIsNull() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> bookServiceUseCase.findActiveById(null)
            );

            assertEquals("Book ID cannot be null", exception.getMessage());
            verify(bookRepositoryPort, never()).findActiveById(anyLong());
        }

        @Test
        @DisplayName("Should not return inactive books")
        void shouldNotReturnInactiveBooks() {
            // Arrange
            Long bookId = 1L;
            when(bookRepositoryPort.findActiveById(bookId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(BookNotFoundException.class, () -> bookServiceUseCase.findActiveById(bookId));
        }
    }

    @Nested
    @DisplayName("update() method tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update book successfully")
        void shouldUpdateBookSuccessfully() {
            // Arrange
            Book bookToUpdate = new Book();
            bookToUpdate.setId(1L);
            bookToUpdate.setTitle("Updated Title");
            bookToUpdate.setAuthor("Updated Author");
            bookToUpdate.setBookGenre(BookGenre.HISTORICAL_FICTION);

            when(bookRepositoryPort.findById(1L)).thenReturn(Optional.of(testBook));
            when(bookRepositoryPort.save(any(Book.class))).thenReturn(bookToUpdate);

            // Act
            Book result = bookServiceUseCase.update(bookToUpdate);

            // Assert
            assertNotNull(result);
            assertNotNull(bookToUpdate.getUpdatedAt());
            verify(bookRepositoryPort, times(1)).findById(1L);
            verify(bookRepositoryPort, times(1)).save(bookToUpdate);
        }

        @Test
        @DisplayName("Should update updatedAt timestamp")
        void shouldUpdateTimestamp() {
            // Arrange
            Book bookToUpdate = new Book();
            bookToUpdate.setId(1L);
            bookToUpdate.setTitle("Updated Title");
            bookToUpdate.setAuthor("Updated Author");
            bookToUpdate.setBookGenre(BookGenre.ROMANCE);

            when(bookRepositoryPort.findById(1L)).thenReturn(Optional.of(testBook));
            when(bookRepositoryPort.save(any(Book.class))).thenReturn(bookToUpdate);

            LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);

            // Act
            bookServiceUseCase.update(bookToUpdate);

            LocalDateTime afterUpdate = LocalDateTime.now().plusSeconds(1);

            // Assert
            assertNotNull(bookToUpdate.getUpdatedAt());
            assertTrue(bookToUpdate.getUpdatedAt().isAfter(beforeUpdate));
            assertTrue(bookToUpdate.getUpdatedAt().isBefore(afterUpdate));
        }

        @Test
        @DisplayName("Should throw BookNotFoundException when book does not exist")
        void shouldThrowExceptionWhenBookDoesNotExist() {
            // Arrange
            Book bookToUpdate = new Book();
            bookToUpdate.setId(999L);
            bookToUpdate.setTitle("Updated Title");

            when(bookRepositoryPort.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(BookNotFoundException.class, () -> bookServiceUseCase.update(bookToUpdate));
            verify(bookRepositoryPort, times(1)).findById(999L);
            verify(bookRepositoryPort, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is null")
        void shouldThrowExceptionWhenIdIsNull() {
            // Arrange
            Book bookToUpdate = new Book();
            bookToUpdate.setTitle("Updated Title");

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> bookServiceUseCase.update(bookToUpdate)
            );

            assertEquals("Book ID cannot be null", exception.getMessage());
            verify(bookRepositoryPort, never()).findById(anyLong());
            verify(bookRepositoryPort, never()).save(any(Book.class));
        }
    }

    @Nested
    @DisplayName("deactivate() method tests")
    class DeactivateTests {

        @Test
        @DisplayName("Should deactivate book successfully (soft delete)")
        void shouldDeactivateBookSuccessfully() {
            // Arrange
            Long bookId = 1L;
            when(bookRepositoryPort.findById(bookId)).thenReturn(Optional.of(testBook));
            when(bookRepositoryPort.save(any(Book.class))).thenReturn(testBook);

            // Act
            bookServiceUseCase.deactivate(bookId);

            // Assert
            assertFalse(testBook.getActive(), "Book should be marked as inactive");
            assertNotNull(testBook.getUpdatedAt());
            verify(bookRepositoryPort, times(1)).findById(bookId);
            verify(bookRepositoryPort, times(1)).save(testBook);
        }

        @Test
        @DisplayName("Should update updatedAt timestamp when deactivating")
        void shouldUpdateTimestampWhenDeactivating() {
            // Arrange
            Long bookId = 1L;
            when(bookRepositoryPort.findById(bookId)).thenReturn(Optional.of(testBook));
            when(bookRepositoryPort.save(any(Book.class))).thenReturn(testBook);

            LocalDateTime beforeDeactivate = LocalDateTime.now().minusSeconds(1);

            // Act
            bookServiceUseCase.deactivate(bookId);

            LocalDateTime afterDeactivate = LocalDateTime.now().plusSeconds(1);

            // Assert
            assertNotNull(testBook.getUpdatedAt());
            assertTrue(testBook.getUpdatedAt().isAfter(beforeDeactivate));
            assertTrue(testBook.getUpdatedAt().isBefore(afterDeactivate));
        }

        @Test
        @DisplayName("Should throw BookNotFoundException when book does not exist")
        void shouldThrowExceptionWhenBookDoesNotExist() {
            // Arrange
            Long bookId = 999L;
            when(bookRepositoryPort.findById(bookId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(BookNotFoundException.class, () -> bookServiceUseCase.deactivate(bookId));
            verify(bookRepositoryPort, times(1)).findById(bookId);
            verify(bookRepositoryPort, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is null")
        void shouldThrowExceptionWhenIdIsNull() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> bookServiceUseCase.deactivate(null)
            );

            assertEquals("Book ID cannot be null", exception.getMessage());
            verify(bookRepositoryPort, never()).findById(anyLong());
            verify(bookRepositoryPort, never()).save(any(Book.class));
        }
    }

    @Nested
    @DisplayName("getAllWithFilters() method tests")
    class GetAllWithFiltersTests {

        @Test
        @DisplayName("Should return paginated results with filters")
        void shouldReturnPaginatedResultsWithFilters() {
            // Arrange
            BookFilter filter = new BookFilter("Test", "Author", BookGenre.FICTION, true);
            PaginationQuery pagination = new PaginationQuery(0, 10, "id", "ASC");

            List<Book> books = List.of(testBook);
            PaginatedResult<Book> expectedResult = new PaginatedResult<>(
                    books,
                    0,
                    10,
                    1,
                    1
            );

            when(bookRepositoryPort.findAllWithFilters(filter, pagination)).thenReturn(expectedResult);

            // Act
            PaginatedResult<Book> result = bookServiceUseCase.getAllWithFilters(filter, pagination);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.content().size());
            assertEquals(1, result.totalElements());
            assertEquals(0, result.pageNumber());
            assertEquals(10, result.pageSize());
            verify(bookRepositoryPort, times(1)).findAllWithFilters(filter, pagination);
        }

        @Test
        @DisplayName("Should handle empty results")
        void shouldHandleEmptyResults() {
            // Arrange
            BookFilter filter = new BookFilter(null, null, null, true);
            PaginationQuery pagination = new PaginationQuery(0, 10, "id", "ASC");

            PaginatedResult<Book> expectedResult = new PaginatedResult<>(
                    List.of(),
                    0,
                    10,
                    0,
                    0
            );

            when(bookRepositoryPort.findAllWithFilters(filter, pagination)).thenReturn(expectedResult);

            // Act
            PaginatedResult<Book> result = bookServiceUseCase.getAllWithFilters(filter, pagination);

            // Assert
            assertNotNull(result);
            assertTrue(result.content().isEmpty());
            assertEquals(0, result.totalElements());
        }

        @Test
        @DisplayName("Should filter by author only")
        void shouldFilterByAuthorOnly() {
            // Arrange
            BookFilter filter = new BookFilter(null, "Test Author", null, true);
            PaginationQuery pagination = new PaginationQuery(0, 10, "id", "ASC");

            List<Book> books = List.of(testBook);
            PaginatedResult<Book> expectedResult = new PaginatedResult<>(books, 0, 10, 1, 1);

            when(bookRepositoryPort.findAllWithFilters(filter, pagination)).thenReturn(expectedResult);

            // Act
            PaginatedResult<Book> result = bookServiceUseCase.getAllWithFilters(filter, pagination);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.content().size());
            verify(bookRepositoryPort, times(1)).findAllWithFilters(filter, pagination);
        }

        @Test
        @DisplayName("Should filter by title only")
        void shouldFilterByTitleOnly() {
            // Arrange
            BookFilter filter = new BookFilter("Test Book", null, null, true);
            PaginationQuery pagination = new PaginationQuery(0, 10, "id", "ASC");

            List<Book> books = List.of(testBook);
            PaginatedResult<Book> expectedResult = new PaginatedResult<>(books, 0, 10, 1, 1);

            when(bookRepositoryPort.findAllWithFilters(filter, pagination)).thenReturn(expectedResult);

            // Act
            PaginatedResult<Book> result = bookServiceUseCase.getAllWithFilters(filter, pagination);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.content().size());
        }

        @Test
        @DisplayName("Should filter by genre only")
        void shouldFilterByGenreOnly() {
            // Arrange
            BookFilter filter = new BookFilter(null, null, BookGenre.FICTION, true);
            PaginationQuery pagination = new PaginationQuery(0, 10, "id", "ASC");

            List<Book> books = List.of(testBook);
            PaginatedResult<Book> expectedResult = new PaginatedResult<>(books, 0, 10, 1, 1);

            when(bookRepositoryPort.findAllWithFilters(filter, pagination)).thenReturn(expectedResult);

            // Act
            PaginatedResult<Book> result = bookServiceUseCase.getAllWithFilters(filter, pagination);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.content().size());
        }

        @Test
        @DisplayName("Should pass pagination parameters correctly")
        void shouldPassPaginationParametersCorrectly() {
            // Arrange
            BookFilter filter = new BookFilter(null, null, null, true);
            PaginationQuery pagination = new PaginationQuery(2, 20, "title", "DESC");

            PaginatedResult<Book> expectedResult = new PaginatedResult<>(List.of(), 2, 20, 0, 0);

            when(bookRepositoryPort.findAllWithFilters(filter, pagination)).thenReturn(expectedResult);

            // Act
            bookServiceUseCase.getAllWithFilters(filter, pagination);

            // Assert
            verify(bookRepositoryPort, times(1)).findAllWithFilters(filter, pagination);
        }
    }
}
