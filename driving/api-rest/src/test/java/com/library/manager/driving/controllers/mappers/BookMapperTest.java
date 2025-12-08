package com.library.manager.driving.controllers.mappers;

import com.library.manager.domain.Book;
import com.library.manager.domain.BookGenre;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.driving.controllers.models.BookRequest;
import com.library.manager.driving.controllers.models.BookResponse;
import com.library.manager.driving.controllers.models.BooksResponse;
import com.library.manager.driving.controllers.models.Pagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookMapper Tests")
class BookMapperTest {

    private BookMapper bookMapper;
    private Book testBook;
    private BookRequest testBookRequest;

    @BeforeEach
    void setUp() {
        bookMapper = Mappers.getMapper(BookMapper.class);

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setBookGenre(BookGenre.FICTION);
        testBook.setPages(300);
        testBook.setPublicationYear(2024);
        testBook.setActive(true);
        testBook.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0, 0));
        testBook.setUpdatedAt(LocalDateTime.of(2024, 1, 1, 12, 0, 0));

        testBookRequest = new BookRequest();
        testBookRequest.setTitle("Test Book");
        testBookRequest.setAuthor("Test Author");
        testBookRequest.setBookGenre(BookRequest.BookGenreEnum.FICTION);
        testBookRequest.setPages(300);
        testBookRequest.setPublicationYear(2024);
    }

    @Nested
    @DisplayName("toBookResponse() mapping tests")
    class ToBookResponseTests {

        @Test
        @DisplayName("Should map Book to BookResponse correctly")
        void shouldMapBookToBookResponse() {
            // Act
            BookResponse result = bookMapper.toBookResponse(testBook);

            // Assert
            assertNotNull(result);
            assertEquals(testBook.getId(), result.getId());
            assertEquals(testBook.getTitle(), result.getTitle());
            assertEquals(testBook.getAuthor(), result.getAuthor());
            assertEquals(testBook.getPages(), result.getPages());
            assertEquals(testBook.getPublicationYear(), result.getPublicationYear());
        }

        @Test
        @DisplayName("Should convert BookGenre enum to String display name")
        void shouldConvertBookGenreToString() {
            // Act
            BookResponse result = bookMapper.toBookResponse(testBook);

            // Assert
            assertNotNull(result.getBookGenre());
            assertEquals(BookGenre.FICTION.getDisplayName(), result.getBookGenre());
        }

        @Test
        @DisplayName("Should convert LocalDateTime to OffsetDateTime for createdAt")
        void shouldConvertLocalDateTimeToOffsetDateTimeForCreatedAt() {
            // Act
            BookResponse result = bookMapper.toBookResponse(testBook);

            // Assert
            assertNotNull(result.getCreatedAt());
            assertInstanceOf(OffsetDateTime.class, result.getCreatedAt());
            assertEquals(ZoneOffset.UTC, result.getCreatedAt().getOffset());
        }

        @Test
        @DisplayName("Should convert LocalDateTime to OffsetDateTime for updatedAt")
        void shouldConvertLocalDateTimeToOffsetDateTimeForUpdatedAt() {
            // Act
            BookResponse result = bookMapper.toBookResponse(testBook);

            // Assert
            assertNotNull(result.getUpdatedAt());
            assertInstanceOf(OffsetDateTime.class, result.getUpdatedAt());
            assertEquals(ZoneOffset.UTC, result.getUpdatedAt().getOffset());
        }

        @Test
        @DisplayName("Should handle null timestamps")
        void shouldHandleNullTimestamps() {
            // Arrange
            testBook.setCreatedAt(null);
            testBook.setUpdatedAt(null);

            // Act
            BookResponse result = bookMapper.toBookResponse(testBook);

            // Assert
            assertNull(result.getCreatedAt());
            assertNull(result.getUpdatedAt());
        }

        @Test
        @DisplayName("Should map all BookGenre values correctly")
        void shouldMapAllBookGenreValues() {
            // Test each genre
            for (BookGenre genre : BookGenre.values()) {
                testBook.setBookGenre(genre);
                BookResponse result = bookMapper.toBookResponse(testBook);
                assertEquals(genre.getDisplayName(), result.getBookGenre());
            }
        }
    }

    @Nested
    @DisplayName("toBook() mapping tests")
    class ToBookTests {

        @Test
        @DisplayName("Should map BookRequest to Book correctly")
        void shouldMapBookRequestToBook() {
            // Act
            Book result = bookMapper.toBook(testBookRequest);

            // Assert
            assertNotNull(result);
            assertEquals(testBookRequest.getTitle(), result.getTitle());
            assertEquals(testBookRequest.getAuthor(), result.getAuthor());
            assertEquals(testBookRequest.getPages(), result.getPages());
            assertEquals(testBookRequest.getPublicationYear(), result.getPublicationYear());
        }

        @Test
        @DisplayName("Should convert String enum to BookGenre")
        void shouldConvertStringToBookGenre() {
            // Act
            Book result = bookMapper.toBook(testBookRequest);

            // Assert
            assertNotNull(result.getBookGenre());
            assertEquals(BookGenre.FICTION, result.getBookGenre());
        }

        @Test
        @DisplayName("Should map all BookRequest.BookGenreEnum values correctly")
        void shouldMapAllBookRequestGenreValues() {
            // Test each genre
            for (BookRequest.BookGenreEnum genreEnum : BookRequest.BookGenreEnum.values()) {
                testBookRequest.setBookGenre(genreEnum);
                Book result = bookMapper.toBook(testBookRequest);
                assertEquals(BookGenre.valueOf(genreEnum.getValue()), result.getBookGenre());
            }
        }
    }

    @Nested
    @DisplayName("toListBookResponse() mapping tests")
    class ToListBookResponseTests {

        @Test
        @DisplayName("Should map list of Books to list of BookResponses")
        void shouldMapListOfBooks() {
            // Arrange
            Book book2 = new Book();
            book2.setId(2L);
            book2.setTitle("Book 2");
            book2.setAuthor("Author 2");
            book2.setBookGenre(BookGenre.MYSTERY);
            book2.setCreatedAt(LocalDateTime.now());
            book2.setUpdatedAt(LocalDateTime.now());

            List<Book> books = List.of(testBook, book2);

            // Act
            List<BookResponse> result = bookMapper.toListBookResponse(books);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should handle empty list")
        void shouldHandleEmptyList() {
            // Act
            List<BookResponse> result = bookMapper.toListBookResponse(List.of());

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("toPagination() mapping tests")
    class ToPaginationTests {

        @Test
        @DisplayName("Should map PaginatedResult to Pagination")
        void shouldMapPaginatedResultToPagination() {
            // Arrange
            PaginatedResult<Book> paginatedResult = new PaginatedResult<>(
                    List.of(testBook),
                    100L,
                    10,
                    2,
                    10
            );

            // Act
            Pagination result = bookMapper.toPagination(paginatedResult);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.getNumber());
            assertEquals(10, result.getSize());
            assertEquals(100L, result.getTotalElements());
            assertEquals(10, result.getTotalPages());
        }

        @Test
        @DisplayName("Should handle first page")
        void shouldHandleFirstPage() {
            // Arrange
            PaginatedResult<Book> paginatedResult = new PaginatedResult<>(
                    List.of(testBook),
                    50L,
                    5,
                    1,
                    10
            );

            // Act
            Pagination result = bookMapper.toPagination(paginatedResult);

            // Assert
            assertEquals(1, result.getNumber());
            assertEquals(10, result.getSize());
        }

        @Test
        @DisplayName("Should handle empty page")
        void shouldHandleEmptyPage() {
            // Arrange
            PaginatedResult<Book> paginatedResult = new PaginatedResult<>(
                    List.of(),
                    0L,
                    0,
                    0,
                    10
            );

            // Act
            Pagination result = bookMapper.toPagination(paginatedResult);

            // Assert
            assertEquals(0, result.getTotalElements());
            assertEquals(0, result.getTotalPages());
        }
    }

    @Nested
    @DisplayName("toFilter() method tests")
    class ToFilterTests {

        @Test
        @DisplayName("Should create filter with all parameters")
        void shouldCreateFilterWithAllParameters() {
            // Act
            BookFilter result = bookMapper.toFilter("Test Author", "Test Title", BookGenre.FICTION);

            // Assert
            assertNotNull(result);
            assertEquals("Test Title", result.title());
            assertEquals("Test Author", result.author());
            assertEquals(BookGenre.FICTION, result.bookGenre());
            assertTrue(result.active());
        }

        @Test
        @DisplayName("Should create filter with null title")
        void shouldCreateFilterWithNullTitle() {
            // Act
            BookFilter result = bookMapper.toFilter("Test Author", null, BookGenre.FICTION);

            // Assert
            assertNull(result.title());
            assertEquals("Test Author", result.author());
            assertEquals(BookGenre.FICTION, result.bookGenre());
            assertTrue(result.active());
        }

        @Test
        @DisplayName("Should create filter with null author")
        void shouldCreateFilterWithNullAuthor() {
            // Act
            BookFilter result = bookMapper.toFilter(null, "Test Title", BookGenre.MYSTERY);

            // Assert
            assertEquals("Test Title", result.title());
            assertNull(result.author());
            assertEquals(BookGenre.MYSTERY, result.bookGenre());
            assertTrue(result.active());
        }

        @Test
        @DisplayName("Should create filter with null genre")
        void shouldCreateFilterWithNullGenre() {
            // Act
            BookFilter result = bookMapper.toFilter("Test Author", "Test Title", null);

            // Assert
            assertEquals("Test Title", result.title());
            assertEquals("Test Author", result.author());
            assertNull(result.bookGenre());
            assertTrue(result.active());
        }

        @Test
        @DisplayName("Should always set active to true")
        void shouldAlwaysSetActiveToTrue() {
            // Act
            BookFilter result1 = bookMapper.toFilter(null, null, null);
            BookFilter result2 = bookMapper.toFilter("Title", "Author", BookGenre.FANTASY);

            // Assert
            assertTrue(result1.active());
            assertTrue(result2.active());
        }

        @Test
        @DisplayName("Should create filter with all null parameters")
        void shouldCreateFilterWithAllNullParameters() {
            // Act
            BookFilter result = bookMapper.toFilter(null, null, null);

            // Assert
            assertNotNull(result);
            assertNull(result.title());
            assertNull(result.author());
            assertNull(result.bookGenre());
            assertTrue(result.active());
        }
    }

    @Nested
    @DisplayName("toResponse() method tests")
    class ToResponseTests {

        @Test
        @DisplayName("Should create BooksResponse with content and pagination")
        void shouldCreateBooksResponse() {
            // Arrange
            PaginatedResult<Book> paginatedResult = new PaginatedResult<>(
                    List.of(testBook),
                    1L,
                    1,
                    1,
                    10
            );

            // Act
            BooksResponse result = bookMapper.toResponse(paginatedResult);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getBooks());
            assertNotNull(result.getPagination());
            assertNotNull(result.getPagination().getTimestamp());
        }

        @Test
        @DisplayName("Should set timestamp in UTC")
        void shouldSetTimestampInUtc() {
            // Arrange
            PaginatedResult<Book> paginatedResult = new PaginatedResult<>(
                    List.of(testBook),
                    1L,
                    1,
                    1,
                    10
            );

            // Act
            BooksResponse result = bookMapper.toResponse(paginatedResult);

            // Assert
            assertNotNull(result.getPagination().getTimestamp());
            assertEquals(ZoneOffset.UTC, result.getPagination().getTimestamp().getOffset());
            assertEquals(0, result.getPagination().getTimestamp().getNano());
        }

        @Test
        @DisplayName("Should map books list correctly")
        void shouldMapBooksListCorrectly() {
            // Arrange
            Book book2 = new Book();
            book2.setId(2L);
            book2.setTitle("Book 2");
            book2.setAuthor("Author 2");
            book2.setBookGenre(BookGenre.SCIENCE_FICTION);
            book2.setCreatedAt(LocalDateTime.now());
            book2.setUpdatedAt(LocalDateTime.now());

            PaginatedResult<Book> paginatedResult = new PaginatedResult<>(
                    List.of(testBook, book2),
                    2L,
                    1,
                    1,
                    10
            );

            // Act
            BooksResponse result = bookMapper.toResponse(paginatedResult);

            // Assert
            assertNotNull(result.getBooks());
            assertEquals(2, result.getBooks().size());
        }

        @Test
        @DisplayName("Should handle empty results")
        void shouldHandleEmptyResults() {
            // Arrange
            PaginatedResult<Book> paginatedResult = new PaginatedResult<>(
                    List.of(),
                    0L,
                    0,
                    0,
                    10
            );

            // Act
            BooksResponse result = bookMapper.toResponse(paginatedResult);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getBooks());
            assertTrue(result.getBooks().isEmpty());
            assertEquals(0, result.getPagination().getTotalElements());
        }
    }

    @Nested
    @DisplayName("Helper method tests")
    class HelperMethodTests {

        @Test
        @DisplayName("enumToString should convert BookGenre to display name")
        void shouldConvertEnumToString() {
            // Act
            String result = bookMapper.enumToString(BookGenre.SCIENCE_FICTION);

            // Assert
            assertEquals(BookGenre.SCIENCE_FICTION.getDisplayName(), result);
        }

        @Test
        @DisplayName("stringToEnum should convert BookRequest.BookGenreEnum to BookGenre")
        void shouldConvertStringToEnum() {
            // Act
            BookGenre result = bookMapper.stringToEnum(BookRequest.BookGenreEnum.FANTASY);

            // Assert
            assertEquals(BookGenre.FANTASY, result);
        }

        @Test
        @DisplayName("getOffsetDateTime should convert LocalDateTime to OffsetDateTime in UTC")
        void shouldConvertLocalDateTimeToOffsetDateTime() {
            // Arrange
            LocalDateTime localDateTime = LocalDateTime.of(2024, 6, 15, 10, 30, 45);

            // Act
            OffsetDateTime result = bookMapper.getOffsetDateTime(localDateTime);

            // Assert
            assertNotNull(result);
            assertEquals(ZoneOffset.UTC, result.getOffset());
            assertEquals(2024, result.getYear());
            assertEquals(6, result.getMonthValue());
            assertEquals(15, result.getDayOfMonth());
            assertEquals(10, result.getHour());
            assertEquals(30, result.getMinute());
            assertEquals(45, result.getSecond());
        }

        @Test
        @DisplayName("getOffsetDateTime should handle null")
        void shouldHandleNullLocalDateTime() {
            // Act
            OffsetDateTime result = bookMapper.getOffsetDateTime(null);

            // Assert
            assertNull(result);
        }
    }
}
