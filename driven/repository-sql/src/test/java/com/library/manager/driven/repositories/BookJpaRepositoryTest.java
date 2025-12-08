package com.library.manager.driven.repositories;

import com.library.manager.domain.BookGenre;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.driven.repositories.models.BookEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@DisplayName("BookJpaRepository Integration Tests")
class BookJpaRepositoryTest {

    @Autowired
    private BookJpaRepository bookJpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private BookEntity testBook1;
    private BookEntity testBook2;
    private BookEntity inactiveBook;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        testBook1 = BookEntity.builder()
                .author("George Orwell")
                .title("1984")
                .bookGenre(BookGenre.FICTION)
                .pages(328)
                .publicationYear(1949)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        testBook2 = BookEntity.builder()
                .author("J.K. Rowling")
                .title("Harry Potter and the Philosopher's Stone")
                .bookGenre(BookGenre.FANTASY)
                .pages(223)
                .publicationYear(1997)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        inactiveBook = BookEntity.builder()
                .author("Inactive Author")
                .title("Inactive Book")
                .bookGenre(BookGenre.MYSTERY)
                .pages(200)
                .publicationYear(2000)
                .active(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        entityManager.persist(testBook1);
        entityManager.persist(testBook2);
        entityManager.persist(inactiveBook);
        entityManager.flush();
    }

    @Nested
    @DisplayName("findByIdAndActiveTrue() method tests")
    class FindByIdAndActiveTrueTests {

        @Test
        @DisplayName("Should find active book by ID")
        void shouldFindActiveBookById() {
            // Act
            Optional<BookEntity> result = bookJpaRepository.findByIdAndActiveTrue(testBook1.getId());

            // Assert
            assertTrue(result.isPresent());
            assertEquals("1984", result.get().getTitle());
            assertEquals("George Orwell", result.get().getAuthor());
            assertTrue(result.get().getActive());
        }

        @Test
        @DisplayName("Should not find inactive book")
        void shouldNotFindInactiveBook() {
            // Act
            Optional<BookEntity> result = bookJpaRepository.findByIdAndActiveTrue(inactiveBook.getId());

            // Assert
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should return empty for non-existent ID")
        void shouldReturnEmptyForNonExistentId() {
            // Act
            Optional<BookEntity> result = bookJpaRepository.findByIdAndActiveTrue(999L);

            // Assert
            assertFalse(result.isPresent());
        }
    }

    @Nested
    @DisplayName("findAllWithFilters() method tests")
    class FindAllWithFiltersTests {

        @Test
        @DisplayName("Should find all active books with no filters")
        void shouldFindAllActiveBooksWithNoFilters() {
            // Arrange
            BookFilter filter = new BookFilter(null, null, null, true);
            Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

            // Act
            Page<BookEntity> result = bookJpaRepository.findAllWithFilters(filter, pageable);

            // Assert
            assertEquals(2, result.getTotalElements());
            assertEquals(2, result.getContent().size());
            assertTrue(result.getContent().stream().allMatch(BookEntity::getActive));
        }

        @Test
        @DisplayName("Should filter by title (case insensitive)")
        void shouldFilterByTitle() {
            // Arrange
            BookFilter filter = new BookFilter("harry", null, null, true);
            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<BookEntity> result = bookJpaRepository.findAllWithFilters(filter, pageable);

            // Assert
            assertEquals(1, result.getTotalElements());
            assertEquals("Harry Potter and the Philosopher's Stone", result.getContent().get(0).getTitle());
        }

        @Test
        @DisplayName("Should filter by title with partial match")
        void shouldFilterByTitlePartialMatch() {
            // Arrange
            BookFilter filter = new BookFilter("potter", null, null, true);
            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<BookEntity> result = bookJpaRepository.findAllWithFilters(filter, pageable);

            // Assert
            assertEquals(1, result.getTotalElements());
            assertTrue(result.getContent().get(0).getTitle().toLowerCase().contains("potter"));
        }

        @Test
        @DisplayName("Should filter by author (case insensitive)")
        void shouldFilterByAuthor() {
            // Arrange
            BookFilter filter = new BookFilter(null, "george orwell", null, true);
            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<BookEntity> result = bookJpaRepository.findAllWithFilters(filter, pageable);

            // Assert
            assertEquals(1, result.getTotalElements());
            assertEquals("George Orwell", result.getContent().get(0).getAuthor());
        }

        @Test
        @DisplayName("Should filter by author with partial match")
        void shouldFilterByAuthorPartialMatch() {
            // Arrange
            BookFilter filter = new BookFilter(null, "rowling", null, true);
            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<BookEntity> result = bookJpaRepository.findAllWithFilters(filter, pageable);

            // Assert
            assertEquals(1, result.getTotalElements());
            assertTrue(result.getContent().get(0).getAuthor().toLowerCase().contains("rowling"));
        }

        @Test
        @DisplayName("Should filter by genre")
        void shouldFilterByGenre() {
            // Arrange
            BookFilter filter = new BookFilter(null, null, BookGenre.FICTION, true);
            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<BookEntity> result = bookJpaRepository.findAllWithFilters(filter, pageable);

            // Assert
            assertEquals(1, result.getTotalElements());
            assertEquals(BookGenre.FICTION, result.getContent().get(0).getBookGenre());
        }

        @Test
        @DisplayName("Should filter by active status")
        void shouldFilterByActiveStatus() {
            // Arrange
            BookFilter filterActive = new BookFilter(null, null, null, true);
            BookFilter filterInactive = new BookFilter(null, null, null, false);
            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<BookEntity> activeResults = bookJpaRepository.findAllWithFilters(filterActive, pageable);
            Page<BookEntity> inactiveResults = bookJpaRepository.findAllWithFilters(filterInactive, pageable);

            // Assert
            assertEquals(2, activeResults.getTotalElements());
            assertEquals(1, inactiveResults.getTotalElements());
            assertTrue(activeResults.getContent().stream().allMatch(BookEntity::getActive));
            assertTrue(inactiveResults.getContent().stream().noneMatch(BookEntity::getActive));
        }

        @Test
        @DisplayName("Should combine multiple filters")
        void shouldCombineMultipleFilters() {
            // Arrange
            BookFilter filter = new BookFilter("harry", "rowling", BookGenre.FANTASY, true);
            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<BookEntity> result = bookJpaRepository.findAllWithFilters(filter, pageable);

            // Assert
            assertEquals(1, result.getTotalElements());
            BookEntity book = result.getContent().get(0);
            assertTrue(book.getTitle().toLowerCase().contains("harry"));
            assertTrue(book.getAuthor().toLowerCase().contains("rowling"));
            assertEquals(BookGenre.FANTASY, book.getBookGenre());
            assertTrue(book.getActive());
        }

        @Test
        @DisplayName("Should return empty page when no matches found")
        void shouldReturnEmptyPageWhenNoMatches() {
            // Arrange
            BookFilter filter = new BookFilter("NonExistentTitle", null, null, true);
            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<BookEntity> result = bookJpaRepository.findAllWithFilters(filter, pageable);

            // Assert
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());
        }

        @Test
        @DisplayName("Should apply pagination correctly")
        void shouldApplyPaginationCorrectly() {
            // Arrange - Add more books for pagination test
            for (int i = 0; i < 15; i++) {
                BookEntity book = BookEntity.builder()
                        .author("Author " + i)
                        .title("Book " + i)
                        .bookGenre(BookGenre.FICTION)
                        .pages(100 + i)
                        .publicationYear(2000 + i)
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                entityManager.persist(book);
            }
            entityManager.flush();

            BookFilter filter = new BookFilter(null, null, null, true);
            Pageable page1 = PageRequest.of(0, 5);
            Pageable page2 = PageRequest.of(1, 5);

            // Act
            Page<BookEntity> result1 = bookJpaRepository.findAllWithFilters(filter, page1);
            Page<BookEntity> result2 = bookJpaRepository.findAllWithFilters(filter, page2);

            // Assert
            assertEquals(5, result1.getContent().size());
            assertEquals(5, result2.getContent().size());
            assertTrue(result1.getTotalElements() >= 15);
            assertNotEquals(result1.getContent().get(0).getId(), result2.getContent().get(0).getId());
        }

        @Test
        @DisplayName("Should apply sorting correctly")
        void shouldApplySortingCorrectly() {
            // Arrange
            BookFilter filter = new BookFilter(null, null, null, true);
            Pageable pageableAsc = PageRequest.of(0, 10, Sort.by("title").ascending());
            Pageable pageableDesc = PageRequest.of(0, 10, Sort.by("title").descending());

            // Act
            Page<BookEntity> ascResults = bookJpaRepository.findAllWithFilters(filter, pageableAsc);
            Page<BookEntity> descResults = bookJpaRepository.findAllWithFilters(filter, pageableDesc);

            // Assert
            assertEquals(2, ascResults.getTotalElements());
            assertEquals("1984", ascResults.getContent().get(0).getTitle());
            assertEquals("Harry Potter and the Philosopher's Stone", descResults.getContent().get(0).getTitle());
        }

        @Test
        @DisplayName("Should handle null filter values")
        void shouldHandleNullFilterValues() {
            // Arrange
            BookFilter filter = new BookFilter(null, null, null, true);
            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<BookEntity> result = bookJpaRepository.findAllWithFilters(filter, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("JpaRepository standard methods tests")
    class StandardJpaMethodsTests {

        @Test
        @DisplayName("Should save new book")
        void shouldSaveNewBook() {
            // Arrange
            BookEntity newBook = BookEntity.builder()
                    .author("New Author")
                    .title("New Book")
                    .bookGenre(BookGenre.SCIENCE_FICTION)
                    .pages(250)
                    .publicationYear(2024)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Act
            BookEntity saved = bookJpaRepository.save(newBook);

            // Assert
            assertNotNull(saved.getId());
            assertEquals("New Book", saved.getTitle());
            assertEquals("New Author", saved.getAuthor());
        }

        @Test
        @DisplayName("Should find by ID")
        void shouldFindById() {
            // Act
            Optional<BookEntity> result = bookJpaRepository.findById(testBook1.getId());

            // Assert
            assertTrue(result.isPresent());
            assertEquals("1984", result.get().getTitle());
        }

        @Test
        @DisplayName("Should find by ID regardless of active status")
        void shouldFindByIdRegardlessOfActiveStatus() {
            // Act
            Optional<BookEntity> result = bookJpaRepository.findById(inactiveBook.getId());

            // Assert
            assertTrue(result.isPresent());
            assertFalse(result.get().getActive());
        }

        @Test
        @DisplayName("Should update existing book")
        void shouldUpdateExistingBook() {
            // Arrange
            testBook1.setTitle("Updated Title");
            testBook1.setUpdatedAt(LocalDateTime.now());

            // Act
            BookEntity updated = bookJpaRepository.save(testBook1);

            // Assert
            assertEquals("Updated Title", updated.getTitle());
            assertEquals(testBook1.getId(), updated.getId());
        }

        @Test
        @DisplayName("Should delete book")
        void shouldDeleteBook() {
            // Arrange
            Long bookId = testBook1.getId();

            // Act
            bookJpaRepository.deleteById(bookId);
            entityManager.flush();

            // Assert
            Optional<BookEntity> result = bookJpaRepository.findById(bookId);
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should count all books")
        void shouldCountAllBooks() {
            // Act
            long count = bookJpaRepository.count();

            // Assert
            assertEquals(3, count);
        }
    }
}
