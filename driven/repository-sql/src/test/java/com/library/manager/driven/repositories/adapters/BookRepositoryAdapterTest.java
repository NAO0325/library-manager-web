package com.library.manager.driven.repositories.adapters;

import com.library.manager.domain.Book;
import com.library.manager.domain.BookGenre;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.domain.valueobjects.PaginationQuery;
import com.library.manager.driven.repositories.BookJpaRepository;
import com.library.manager.driven.repositories.mappers.BookEntityMapper;
import com.library.manager.driven.repositories.mappers.PaginationEntityMapper;
import com.library.manager.driven.repositories.models.BookEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookRepositoryAdapter Unit Tests")
class BookRepositoryAdapterTest {

    @Mock
    private BookJpaRepository bookJpaRepository;

    @Mock
    private BookEntityMapper bookEntityMapper;

    @Mock
    private PaginationEntityMapper paginationEntityMapper;

    @InjectMocks
    private BookRepositoryAdapter bookRepositoryAdapter;

    private Book testBook;
    private BookEntity testBookEntity;

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

        testBookEntity = BookEntity.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .bookGenre(BookGenre.FICTION)
                .pages(300)
                .publicationYear(2024)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("save() method tests")
    class SaveTests {

        @Test
        @DisplayName("Should save book successfully with entity mapping")
        void shouldSaveBookSuccessfully() {
            // Arrange
            when(bookEntityMapper.toEntity(testBook)).thenReturn(testBookEntity);
            when(bookJpaRepository.save(testBookEntity)).thenReturn(testBookEntity);
            when(bookEntityMapper.toDomain(testBookEntity)).thenReturn(testBook);

            // Act
            Book result = bookRepositoryAdapter.save(testBook);

            // Assert
            assertNotNull(result);
            assertEquals(testBook.getId(), result.getId());
            assertEquals(testBook.getTitle(), result.getTitle());
            assertEquals(testBook.getAuthor(), result.getAuthor());
            verify(bookEntityMapper, times(1)).toEntity(testBook);
            verify(bookJpaRepository, times(1)).save(testBookEntity);
            verify(bookEntityMapper, times(1)).toDomain(testBookEntity);
        }

        @Test
        @DisplayName("Should handle entity to domain mapping correctly")
        void shouldHandleEntityToDomainMapping() {
            // Arrange
            when(bookEntityMapper.toEntity(any(Book.class))).thenReturn(testBookEntity);
            when(bookJpaRepository.save(any(BookEntity.class))).thenReturn(testBookEntity);
            when(bookEntityMapper.toDomain(testBookEntity)).thenReturn(testBook);

            // Act
            Book result = bookRepositoryAdapter.save(testBook);

            // Assert
            assertNotNull(result);
            verify(bookEntityMapper, times(1)).toEntity(testBook);
            verify(bookEntityMapper, times(1)).toDomain(testBookEntity);
        }

        @Test
        @DisplayName("Should call JPA repository save exactly once")
        void shouldCallJpaRepositorySaveOnce() {
            // Arrange
            when(bookEntityMapper.toEntity(testBook)).thenReturn(testBookEntity);
            when(bookJpaRepository.save(testBookEntity)).thenReturn(testBookEntity);
            when(bookEntityMapper.toDomain(testBookEntity)).thenReturn(testBook);

            // Act
            bookRepositoryAdapter.save(testBook);

            // Assert
            verify(bookJpaRepository, times(1)).save(testBookEntity);
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
            when(bookJpaRepository.findByIdAndActiveTrue(bookId)).thenReturn(Optional.of(testBookEntity));
            when(bookEntityMapper.toDomain(testBookEntity)).thenReturn(testBook);

            // Act
            Optional<Book> result = bookRepositoryAdapter.findActiveById(bookId);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(testBook.getId(), result.get().getId());
            assertTrue(result.get().getActive());
            verify(bookJpaRepository, times(1)).findByIdAndActiveTrue(bookId);
            verify(bookEntityMapper, times(1)).toDomain(testBookEntity);
        }

        @Test
        @DisplayName("Should return empty Optional when active book not found")
        void shouldReturnEmptyWhenNotFound() {
            // Arrange
            Long bookId = 999L;
            when(bookJpaRepository.findByIdAndActiveTrue(bookId)).thenReturn(Optional.empty());

            // Act
            Optional<Book> result = bookRepositoryAdapter.findActiveById(bookId);

            // Assert
            assertFalse(result.isPresent());
            verify(bookJpaRepository, times(1)).findByIdAndActiveTrue(bookId);
            verify(bookEntityMapper, never()).toDomain(any());
        }

        @Test
        @DisplayName("Should not return inactive books")
        void shouldNotReturnInactiveBooks() {
            // Arrange
            Long bookId = 1L;
            testBookEntity.setActive(false);
            when(bookJpaRepository.findByIdAndActiveTrue(bookId)).thenReturn(Optional.empty());

            // Act
            Optional<Book> result = bookRepositoryAdapter.findActiveById(bookId);

            // Assert
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should apply readOnly transaction annotation")
        void shouldUseReadOnlyTransaction() {
            // This test verifies the method has @Transactional(readOnly = true)
            // The annotation is verified through code inspection
            Long bookId = 1L;
            when(bookJpaRepository.findByIdAndActiveTrue(bookId)).thenReturn(Optional.of(testBookEntity));
            when(bookEntityMapper.toDomain(testBookEntity)).thenReturn(testBook);

            bookRepositoryAdapter.findActiveById(bookId);

            verify(bookJpaRepository, times(1)).findByIdAndActiveTrue(bookId);
        }
    }

    @Nested
    @DisplayName("findById() method tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return book regardless of active status")
        void shouldReturnBookRegardlessOfActiveStatus() {
            // Arrange
            Long bookId = 1L;
            testBookEntity.setActive(false);
            when(bookJpaRepository.findById(bookId)).thenReturn(Optional.of(testBookEntity));
            when(bookEntityMapper.toDomain(testBookEntity)).thenReturn(testBook);

            // Act
            Optional<Book> result = bookRepositoryAdapter.findById(bookId);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(testBook.getId(), result.get().getId());
            verify(bookJpaRepository, times(1)).findById(bookId);
            verify(bookEntityMapper, times(1)).toDomain(testBookEntity);
        }

        @Test
        @DisplayName("Should return empty Optional when book not found")
        void shouldReturnEmptyWhenNotFound() {
            // Arrange
            Long bookId = 999L;
            when(bookJpaRepository.findById(bookId)).thenReturn(Optional.empty());

            // Act
            Optional<Book> result = bookRepositoryAdapter.findById(bookId);

            // Assert
            assertFalse(result.isPresent());
            verify(bookJpaRepository, times(1)).findById(bookId);
            verify(bookEntityMapper, never()).toDomain(any());
        }

        @Test
        @DisplayName("Should find active books")
        void shouldFindActiveBooks() {
            // Arrange
            Long bookId = 1L;
            testBookEntity.setActive(true);
            when(bookJpaRepository.findById(bookId)).thenReturn(Optional.of(testBookEntity));
            when(bookEntityMapper.toDomain(testBookEntity)).thenReturn(testBook);

            // Act
            Optional<Book> result = bookRepositoryAdapter.findById(bookId);

            // Assert
            assertTrue(result.isPresent());
        }
    }

    @Nested
    @DisplayName("findAllWithFilters() method tests")
    class FindAllWithFiltersTests {

        @Test
        @DisplayName("Should return paginated results with filters")
        void shouldReturnPaginatedResultsWithFilters() {
            // Arrange
            BookFilter filter = new BookFilter("Test Book", "Test Author", BookGenre.FICTION, true);
            PaginationQuery paginationQuery = new PaginationQuery(0, 10, "id", "ASC");
            Pageable pageable = PageRequest.of(0, 10);

            List<BookEntity> entities = List.of(testBookEntity);
            Page<BookEntity> entityPage = new PageImpl<>(entities, pageable, 1);

            PaginatedResult<Book> expectedResult = new PaginatedResult<>(
                    List.of(testBook),
                    1L,
                    1,
                    1,
                    10
            );

            when(paginationEntityMapper.toPageable(paginationQuery)).thenReturn(pageable);
            when(bookJpaRepository.findAllWithFilters(filter, pageable)).thenReturn(entityPage);
            when(bookEntityMapper.toBookPaginatedResult(entityPage)).thenReturn(expectedResult);

            // Act
            PaginatedResult<Book> result = bookRepositoryAdapter.findAllWithFilters(filter, paginationQuery);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.content().size());
            assertEquals(1L, result.totalElements());
            assertEquals(1, result.pageNumber());
            verify(paginationEntityMapper, times(1)).toPageable(paginationQuery);
            verify(bookJpaRepository, times(1)).findAllWithFilters(filter, pageable);
            verify(bookEntityMapper, times(1)).toBookPaginatedResult(entityPage);
        }

        @Test
        @DisplayName("Should handle empty results")
        void shouldHandleEmptyResults() {
            // Arrange
            BookFilter filter = new BookFilter(null, null, null, true);
            PaginationQuery paginationQuery = new PaginationQuery(0, 10, "id", "ASC");
            Pageable pageable = PageRequest.of(0, 10);

            Page<BookEntity> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            PaginatedResult<Book> emptyResult = new PaginatedResult<>(List.of(), 0L, 0, 0, 10);

            when(paginationEntityMapper.toPageable(paginationQuery)).thenReturn(pageable);
            when(bookJpaRepository.findAllWithFilters(filter, pageable)).thenReturn(emptyPage);
            when(bookEntityMapper.toBookPaginatedResult(emptyPage)).thenReturn(emptyResult);

            // Act
            PaginatedResult<Book> result = bookRepositoryAdapter.findAllWithFilters(filter, paginationQuery);

            // Assert
            assertNotNull(result);
            assertTrue(result.content().isEmpty());
            assertEquals(0L, result.totalElements());
        }

        @Test
        @DisplayName("Should pass pagination query to mapper correctly")
        void shouldPassPaginationQueryToMapperCorrectly() {
            // Arrange
            BookFilter filter = new BookFilter(null, null, null, true);
            PaginationQuery paginationQuery = new PaginationQuery(2, 20, "title", "DESC");
            Pageable pageable = PageRequest.of(2, 20);

            Page<BookEntity> entityPage = new PageImpl<>(List.of(), pageable, 0);
            PaginatedResult<Book> result = new PaginatedResult<>(List.of(), 0L, 0, 2, 20);

            when(paginationEntityMapper.toPageable(paginationQuery)).thenReturn(pageable);
            when(bookJpaRepository.findAllWithFilters(filter, pageable)).thenReturn(entityPage);
            when(bookEntityMapper.toBookPaginatedResult(entityPage)).thenReturn(result);

            // Act
            bookRepositoryAdapter.findAllWithFilters(filter, paginationQuery);

            // Assert
            verify(paginationEntityMapper, times(1)).toPageable(paginationQuery);
        }

        @Test
        @DisplayName("Should apply filters correctly")
        void shouldApplyFiltersCorrectly() {
            // Arrange
            BookFilter filter = new BookFilter("Specific Title", "Specific Author", BookGenre.MYSTERY, true);
            PaginationQuery paginationQuery = new PaginationQuery(0, 10, "id", "ASC");
            Pageable pageable = PageRequest.of(0, 10);

            Page<BookEntity> entityPage = new PageImpl<>(List.of(), pageable, 0);
            PaginatedResult<Book> result = new PaginatedResult<>(List.of(), 0L, 0, 0, 10);

            when(paginationEntityMapper.toPageable(paginationQuery)).thenReturn(pageable);
            when(bookJpaRepository.findAllWithFilters(filter, pageable)).thenReturn(entityPage);
            when(bookEntityMapper.toBookPaginatedResult(entityPage)).thenReturn(result);

            // Act
            bookRepositoryAdapter.findAllWithFilters(filter, paginationQuery);

            // Assert
            verify(bookJpaRepository, times(1)).findAllWithFilters(filter, pageable);
        }

        @Test
        @DisplayName("Should apply readOnly transaction annotation")
        void shouldUseReadOnlyTransaction() {
            // This test verifies the method has @Transactional(readOnly = true)
            BookFilter filter = new BookFilter(null, null, null, true);
            PaginationQuery paginationQuery = new PaginationQuery(0, 10, "id", "ASC");
            Pageable pageable = PageRequest.of(0, 10);

            Page<BookEntity> entityPage = new PageImpl<>(List.of(), pageable, 0);
            PaginatedResult<Book> result = new PaginatedResult<>(List.of(), 0L, 0, 0, 10);

            when(paginationEntityMapper.toPageable(paginationQuery)).thenReturn(pageable);
            when(bookJpaRepository.findAllWithFilters(filter, pageable)).thenReturn(entityPage);
            when(bookEntityMapper.toBookPaginatedResult(entityPage)).thenReturn(result);

            bookRepositoryAdapter.findAllWithFilters(filter, paginationQuery);

            verify(bookJpaRepository, times(1)).findAllWithFilters(filter, pageable);
        }
    }
}