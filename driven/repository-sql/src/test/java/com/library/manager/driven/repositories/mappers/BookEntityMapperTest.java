package com.library.manager.driven.repositories.mappers;

import com.library.manager.domain.Book;
import com.library.manager.domain.BookGenre;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.driven.repositories.models.BookEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookEntityMapper Tests")
class BookEntityMapperTest {

    private BookEntityMapper bookEntityMapper;
    private Book testBook;
    private BookEntity testBookEntity;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        bookEntityMapper = Mappers.getMapper(BookEntityMapper.class);
        now = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setBookGenre(BookGenre.FICTION);
        testBook.setPages(300);
        testBook.setPublicationYear(2024);
        testBook.setActive(true);
        testBook.setCreatedAt(now);
        testBook.setUpdatedAt(now);

        testBookEntity = BookEntity.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .bookGenre(BookGenre.FICTION)
                .pages(300)
                .publicationYear(2024)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Nested
    @DisplayName("toDomain() mapping tests")
    class ToDomainTests {

        @Test
        @DisplayName("Should map BookEntity to Book domain correctly")
        void shouldMapEntityToDomain() {
            // Act
            Book result = bookEntityMapper.toDomain(testBookEntity);

            // Assert
            assertNotNull(result);
            assertEquals(testBookEntity.getId(), result.getId());
            assertEquals(testBookEntity.getTitle(), result.getTitle());
            assertEquals(testBookEntity.getAuthor(), result.getAuthor());
            assertEquals(testBookEntity.getBookGenre(), result.getBookGenre());
            assertEquals(testBookEntity.getPages(), result.getPages());
            assertEquals(testBookEntity.getPublicationYear(), result.getPublicationYear());
            assertEquals(testBookEntity.getActive(), result.getActive());
            assertEquals(testBookEntity.getCreatedAt(), result.getCreatedAt());
            assertEquals(testBookEntity.getUpdatedAt(), result.getUpdatedAt());
        }

        @Test
        @DisplayName("Should handle all BookGenre values")
        void shouldHandleAllBookGenreValues() {
            for (BookGenre genre : BookGenre.values()) {
                testBookEntity.setBookGenre(genre);
                Book result = bookEntityMapper.toDomain(testBookEntity);
                assertEquals(genre, result.getBookGenre());
            }
        }

        @Test
        @DisplayName("Should map inactive book")
        void shouldMapInactiveBook() {
            // Arrange
            testBookEntity.setActive(false);

            // Act
            Book result = bookEntityMapper.toDomain(testBookEntity);

            // Assert
            assertFalse(result.getActive());
        }

        @Test
        @DisplayName("Should preserve timestamps")
        void shouldPreserveTimestamps() {
            // Act
            Book result = bookEntityMapper.toDomain(testBookEntity);

            // Assert
            assertEquals(testBookEntity.getCreatedAt(), result.getCreatedAt());
            assertEquals(testBookEntity.getUpdatedAt(), result.getUpdatedAt());
        }

        @Test
        @DisplayName("Should handle null optional fields")
        void shouldHandleNullOptionalFields() {
            // Arrange
            testBookEntity.setPages(null);
            testBookEntity.setPublicationYear(null);

            // Act
            Book result = bookEntityMapper.toDomain(testBookEntity);

            // Assert
            assertNotNull(result);
            assertNull(result.getPages());
            assertNull(result.getPublicationYear());
        }
    }

    @Nested
    @DisplayName("toEntity() mapping tests")
    class ToEntityTests {

        @Test
        @DisplayName("Should map Book domain to BookEntity correctly")
        void shouldMapDomainToEntity() {
            // Act
            BookEntity result = bookEntityMapper.toEntity(testBook);

            // Assert
            assertNotNull(result);
            assertEquals(testBook.getId(), result.getId());
            assertEquals(testBook.getTitle(), result.getTitle());
            assertEquals(testBook.getAuthor(), result.getAuthor());
            assertEquals(testBook.getBookGenre(), result.getBookGenre());
            assertEquals(testBook.getPages(), result.getPages());
            assertEquals(testBook.getPublicationYear(), result.getPublicationYear());
            assertEquals(testBook.getActive(), result.getActive());
            assertEquals(testBook.getCreatedAt(), result.getCreatedAt());
            assertEquals(testBook.getUpdatedAt(), result.getUpdatedAt());
        }

        @Test
        @DisplayName("Should handle all BookGenre values")
        void shouldHandleAllBookGenreValues() {
            for (BookGenre genre : BookGenre.values()) {
                testBook.setBookGenre(genre);
                BookEntity result = bookEntityMapper.toEntity(testBook);
                assertEquals(genre, result.getBookGenre());
            }
        }

        @Test
        @DisplayName("Should map inactive book")
        void shouldMapInactiveBook() {
            // Arrange
            testBook.setActive(false);

            // Act
            BookEntity result = bookEntityMapper.toEntity(testBook);

            // Assert
            assertFalse(result.getActive());
        }

        @Test
        @DisplayName("Should preserve timestamps")
        void shouldPreserveTimestamps() {
            // Act
            BookEntity result = bookEntityMapper.toEntity(testBook);

            // Assert
            assertEquals(testBook.getCreatedAt(), result.getCreatedAt());
            assertEquals(testBook.getUpdatedAt(), result.getUpdatedAt());
        }

        @Test
        @DisplayName("Should handle null optional fields")
        void shouldHandleNullOptionalFields() {
            // Arrange
            testBook.setPages(null);
            testBook.setPublicationYear(null);

            // Act
            BookEntity result = bookEntityMapper.toEntity(testBook);

            // Assert
            assertNotNull(result);
            assertNull(result.getPages());
            assertNull(result.getPublicationYear());
        }

        @Test
        @DisplayName("Should handle new book without ID")
        void shouldHandleNewBookWithoutId() {
            // Arrange
            testBook.setId(null);

            // Act
            BookEntity result = bookEntityMapper.toEntity(testBook);

            // Assert
            assertNotNull(result);
            assertNull(result.getId());
        }
    }

    @Nested
    @DisplayName("toBookPaginatedResult() mapping tests")
    class ToBookPaginatedResultTests {

        @Test
        @DisplayName("Should map Page<BookEntity> to PaginatedResult<Book>")
        void shouldMapPageToPaginatedResult() {
            // Arrange
            List<BookEntity> entities = List.of(testBookEntity);
            Page<BookEntity> entityPage = new PageImpl<>(entities, PageRequest.of(0, 10), 1);

            // Act
            PaginatedResult<Book> result = bookEntityMapper.toBookPaginatedResult(entityPage);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.content().size());
            assertEquals(1L, result.totalElements());
            assertEquals(1, result.totalPages());
            assertEquals(1, result.pageNumber()); // Note: page number is 0-indexed in Spring but 1-indexed in result
            assertEquals(10, result.pageSize());
        }

        @Test
        @DisplayName("Should handle empty page")
        void shouldHandleEmptyPage() {
            // Arrange
            Page<BookEntity> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

            // Act
            PaginatedResult<Book> result = bookEntityMapper.toBookPaginatedResult(emptyPage);

            // Assert
            assertNotNull(result);
            assertTrue(result.content().isEmpty());
            assertEquals(0L, result.totalElements());
            assertEquals(0, result.totalPages());
            assertEquals(1, result.pageNumber());
            assertEquals(10, result.pageSize());
        }

        @Test
        @DisplayName("Should map multiple books correctly")
        void shouldMapMultipleBooks() {
            // Arrange
            BookEntity entity2 = BookEntity.builder()
                    .id(2L)
                    .title("Book 2")
                    .author("Author 2")
                    .bookGenre(BookGenre.MYSTERY)
                    .active(true)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            List<BookEntity> entities = List.of(testBookEntity, entity2);
            Page<BookEntity> entityPage = new PageImpl<>(entities, PageRequest.of(0, 10), 2);

            // Act
            PaginatedResult<Book> result = bookEntityMapper.toBookPaginatedResult(entityPage);

            // Assert
            assertEquals(2, result.content().size());
            assertEquals(2L, result.totalElements());
            assertEquals(1, result.totalPages());
        }

        @Test
        @DisplayName("Should adjust page number from 0-indexed to 1-indexed")
        void shouldAdjustPageNumber() {
            // Arrange
            Page<BookEntity> entityPage = new PageImpl<>(
                    List.of(testBookEntity),
                    PageRequest.of(2, 10),
                    30
            );

            // Act
            PaginatedResult<Book> result = bookEntityMapper.toBookPaginatedResult(entityPage);

            // Assert
            assertEquals(3, result.pageNumber()); // Page 2 (0-indexed) becomes 3 (1-indexed)
        }

        @Test
        @DisplayName("Should handle different page sizes")
        void shouldHandleDifferentPageSizes() {
            // Arrange
            Page<BookEntity> entityPage = new PageImpl<>(
                    List.of(testBookEntity),
                    PageRequest.of(0, 20),
                    1
            );

            // Act
            PaginatedResult<Book> result = bookEntityMapper.toBookPaginatedResult(entityPage);

            // Assert
            assertEquals(20, result.pageSize());
        }

        @Test
        @DisplayName("Should calculate total pages correctly")
        void shouldCalculateTotalPagesCorrectly() {
            // Arrange - 25 total items with page size 10 = 3 pages
            Page<BookEntity> entityPage = new PageImpl<>(
                    List.of(testBookEntity),
                    PageRequest.of(0, 10),
                    25
            );

            // Act
            PaginatedResult<Book> result = bookEntityMapper.toBookPaginatedResult(entityPage);

            // Assert
            assertEquals(25L, result.totalElements());
            assertEquals(3, result.totalPages());
        }
    }

    @Nested
    @DisplayName("Bidirectional mapping tests")
    class BidirectionalMappingTests {

        @Test
        @DisplayName("Should maintain data integrity when mapping entity to domain and back")
        void shouldMaintainDataIntegrityEntityToDomainToEntity() {
            // Act
            Book domain = bookEntityMapper.toDomain(testBookEntity);
            BookEntity backToEntity = bookEntityMapper.toEntity(domain);

            // Assert
            assertEquals(testBookEntity.getId(), backToEntity.getId());
            assertEquals(testBookEntity.getTitle(), backToEntity.getTitle());
            assertEquals(testBookEntity.getAuthor(), backToEntity.getAuthor());
            assertEquals(testBookEntity.getBookGenre(), backToEntity.getBookGenre());
            assertEquals(testBookEntity.getPages(), backToEntity.getPages());
            assertEquals(testBookEntity.getPublicationYear(), backToEntity.getPublicationYear());
            assertEquals(testBookEntity.getActive(), backToEntity.getActive());
            assertEquals(testBookEntity.getCreatedAt(), backToEntity.getCreatedAt());
            assertEquals(testBookEntity.getUpdatedAt(), backToEntity.getUpdatedAt());
        }

        @Test
        @DisplayName("Should maintain data integrity when mapping domain to entity and back")
        void shouldMaintainDataIntegrityDomainToEntityToDomain() {
            // Act
            BookEntity entity = bookEntityMapper.toEntity(testBook);
            Book backToDomain = bookEntityMapper.toDomain(entity);

            // Assert
            assertEquals(testBook.getId(), backToDomain.getId());
            assertEquals(testBook.getTitle(), backToDomain.getTitle());
            assertEquals(testBook.getAuthor(), backToDomain.getAuthor());
            assertEquals(testBook.getBookGenre(), backToDomain.getBookGenre());
            assertEquals(testBook.getPages(), backToDomain.getPages());
            assertEquals(testBook.getPublicationYear(), backToDomain.getPublicationYear());
            assertEquals(testBook.getActive(), backToDomain.getActive());
            assertEquals(testBook.getCreatedAt(), backToDomain.getCreatedAt());
            assertEquals(testBook.getUpdatedAt(), backToDomain.getUpdatedAt());
        }
    }
}