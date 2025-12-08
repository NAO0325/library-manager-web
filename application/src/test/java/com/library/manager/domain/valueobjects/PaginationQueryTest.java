package com.library.manager.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PaginationQuery Record Tests")
class PaginationQueryTest {

    @Nested
    @DisplayName("Constructor with all parameters")
    class FullConstructorTests {

        @Test
        @DisplayName("Should create PaginationQuery with valid parameters")
        void shouldCreateWithValidParameters() {
            // Act
            PaginationQuery query = new PaginationQuery(0, 10, "title", "ASC");

            // Assert
            assertEquals(0, query.page());
            assertEquals(10, query.pageSize());
            assertEquals("title", query.sortBy());
            assertEquals("ASC", query.sortDirection());
        }

        @Test
        @DisplayName("Should throw exception when page is negative")
        void shouldThrowExceptionWhenPageIsNegative() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new PaginationQuery(-1, 10, "title", "ASC")
            );
            assertEquals("Page number cannot be negative.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when pageSize is zero")
        void shouldThrowExceptionWhenPageSizeIsZero() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new PaginationQuery(0, 0, "title", "ASC")
            );
            assertEquals("Page size must be at least 1.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when pageSize is negative")
        void shouldThrowExceptionWhenPageSizeIsNegative() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new PaginationQuery(0, -5, "title", "ASC")
            );
            assertEquals("Page size must be at least 1.", exception.getMessage());
        }

        @Test
        @DisplayName("Should accept page zero as valid")
        void shouldAcceptPageZero() {
            // Act
            PaginationQuery query = new PaginationQuery(0, 10, "title", "ASC");

            // Assert
            assertEquals(0, query.page());
        }

        @Test
        @DisplayName("Should accept page size of 1 as valid")
        void shouldAcceptPageSizeOne() {
            // Act
            PaginationQuery query = new PaginationQuery(0, 1, "title", "ASC");

            // Assert
            assertEquals(1, query.pageSize());
        }

        @Test
        @DisplayName("Should default sortBy to 'title' when null")
        void shouldDefaultSortByWhenNull() {
            // Act
            PaginationQuery query = new PaginationQuery(0, 10, null, "ASC");

            // Assert
            assertEquals("title", query.sortBy());
        }

        @Test
        @DisplayName("Should default sortBy to 'title' when blank")
        void shouldDefaultSortByWhenBlank() {
            // Act
            PaginationQuery query = new PaginationQuery(0, 10, "  ", "ASC");

            // Assert
            assertEquals("title", query.sortBy());
        }

        @Test
        @DisplayName("Should default sortBy to 'title' when empty")
        void shouldDefaultSortByWhenEmpty() {
            // Act
            PaginationQuery query = new PaginationQuery(0, 10, "", "ASC");

            // Assert
            assertEquals("title", query.sortBy());
        }

        @Test
        @DisplayName("Should default sortDirection to 'asc' when null")
        void shouldDefaultSortDirectionWhenNull() {
            // Act
            PaginationQuery query = new PaginationQuery(0, 10, "title", null);

            // Assert
            assertEquals("asc", query.sortDirection());
        }

        @Test
        @DisplayName("Should default sortDirection to 'asc' when blank")
        void shouldDefaultSortDirectionWhenBlank() {
            // Act
            PaginationQuery query = new PaginationQuery(0, 10, "title", "   ");

            // Assert
            assertEquals("asc", query.sortDirection());
        }

        @Test
        @DisplayName("Should default sortDirection to 'asc' when empty")
        void shouldDefaultSortDirectionWhenEmpty() {
            // Act
            PaginationQuery query = new PaginationQuery(0, 10, "title", "");

            // Assert
            assertEquals("asc", query.sortDirection());
        }

        @Test
        @DisplayName("Should default both sortBy and sortDirection when both are null")
        void shouldDefaultBothWhenNull() {
            // Act
            PaginationQuery query = new PaginationQuery(0, 10, null, null);

            // Assert
            assertEquals("title", query.sortBy());
            assertEquals("asc", query.sortDirection());
        }

        @Test
        @DisplayName("Should preserve custom sortBy value")
        void shouldPreserveCustomSortBy() {
            // Act
            PaginationQuery query = new PaginationQuery(0, 10, "author", "DESC");

            // Assert
            assertEquals("author", query.sortBy());
        }

        @Test
        @DisplayName("Should preserve custom sortDirection value")
        void shouldPreserveCustomSortDirection() {
            // Act
            PaginationQuery query = new PaginationQuery(0, 10, "title", "DESC");

            // Assert
            assertEquals("DESC", query.sortDirection());
        }

        @Test
        @DisplayName("Should handle large page numbers")
        void shouldHandleLargePageNumbers() {
            // Act
            PaginationQuery query = new PaginationQuery(1000, 10, "title", "ASC");

            // Assert
            assertEquals(1000, query.page());
        }

        @Test
        @DisplayName("Should handle large page sizes")
        void shouldHandleLargePageSizes() {
            // Act
            PaginationQuery query = new PaginationQuery(0, 1000, "title", "ASC");

            // Assert
            assertEquals(1000, query.pageSize());
        }
    }

    @Nested
    @DisplayName("Constructor with page and pageSize only")
    class SimpleConstructorTests {

        @Test
        @DisplayName("Should create with default sortBy and sortDirection")
        void shouldCreateWithDefaults() {
            // Act
            PaginationQuery query = new PaginationQuery(0, 10);

            // Assert
            assertEquals(0, query.page());
            assertEquals(10, query.pageSize());
            assertEquals("title", query.sortBy());
            assertEquals("asc", query.sortDirection());
        }

        @Test
        @DisplayName("Should throw exception when page is negative")
        void shouldThrowExceptionWhenPageIsNegative() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new PaginationQuery(-1, 10)
            );
            assertEquals("Page number cannot be negative.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when pageSize is invalid")
        void shouldThrowExceptionWhenPageSizeIsInvalid() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new PaginationQuery(0, 0)
            );
            assertEquals("Page size must be at least 1.", exception.getMessage());
        }

        @Test
        @DisplayName("Should accept various valid page values")
        void shouldAcceptVariousValidPages() {
            // Act & Assert
            assertDoesNotThrow(() -> new PaginationQuery(0, 10));
            assertDoesNotThrow(() -> new PaginationQuery(1, 10));
            assertDoesNotThrow(() -> new PaginationQuery(100, 10));
        }

        @Test
        @DisplayName("Should accept various valid pageSize values")
        void shouldAcceptVariousValidPageSizes() {
            // Act & Assert
            assertDoesNotThrow(() -> new PaginationQuery(0, 1));
            assertDoesNotThrow(() -> new PaginationQuery(0, 50));
            assertDoesNotThrow(() -> new PaginationQuery(0, 100));
        }
    }

    @Nested
    @DisplayName("Record equality and hashCode")
    class EqualityTests {

        @Test
        @DisplayName("Should be equal when all fields are the same")
        void shouldBeEqualWhenFieldsAreSame() {
            // Arrange
            PaginationQuery query1 = new PaginationQuery(0, 10, "title", "ASC");
            PaginationQuery query2 = new PaginationQuery(0, 10, "title", "ASC");

            // Assert
            assertEquals(query1, query2);
            assertEquals(query1.hashCode(), query2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when page differs")
        void shouldNotBeEqualWhenPageDiffers() {
            // Arrange
            PaginationQuery query1 = new PaginationQuery(0, 10, "title", "ASC");
            PaginationQuery query2 = new PaginationQuery(1, 10, "title", "ASC");

            // Assert
            assertNotEquals(query1, query2);
        }

        @Test
        @DisplayName("Should not be equal when pageSize differs")
        void shouldNotBeEqualWhenPageSizeDiffers() {
            // Arrange
            PaginationQuery query1 = new PaginationQuery(0, 10, "title", "ASC");
            PaginationQuery query2 = new PaginationQuery(0, 20, "title", "ASC");

            // Assert
            assertNotEquals(query1, query2);
        }

        @Test
        @DisplayName("Should not be equal when sortBy differs")
        void shouldNotBeEqualWhenSortByDiffers() {
            // Arrange
            PaginationQuery query1 = new PaginationQuery(0, 10, "title", "ASC");
            PaginationQuery query2 = new PaginationQuery(0, 10, "author", "ASC");

            // Assert
            assertNotEquals(query1, query2);
        }

        @Test
        @DisplayName("Should not be equal when sortDirection differs")
        void shouldNotBeEqualWhenSortDirectionDiffers() {
            // Arrange
            PaginationQuery query1 = new PaginationQuery(0, 10, "title", "ASC");
            PaginationQuery query2 = new PaginationQuery(0, 10, "title", "DESC");

            // Assert
            assertNotEquals(query1, query2);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            // Arrange
            PaginationQuery query = new PaginationQuery(0, 10, "title", "ASC");

            // Assert
            assertEquals(query, query);
        }
    }

    @Nested
    @DisplayName("Record toString")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString with all fields")
        void shouldGenerateToStringWithAllFields() {
            // Arrange
            PaginationQuery query = new PaginationQuery(0, 10, "title", "ASC");

            // Act
            String toString = query.toString();

            // Assert
            assertTrue(toString.contains("page=0"));
            assertTrue(toString.contains("pageSize=10"));
            assertTrue(toString.contains("sortBy=title"));
            assertTrue(toString.contains("sortDirection=ASC"));
        }
    }
}
