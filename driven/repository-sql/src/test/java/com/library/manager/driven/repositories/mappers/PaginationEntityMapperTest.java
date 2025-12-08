package com.library.manager.driven.repositories.mappers;

import com.library.manager.domain.valueobjects.PaginationQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PaginationEntityMapper Tests")
class PaginationEntityMapperTest {

    private PaginationEntityMapper paginationEntityMapper;

    @BeforeEach
    void setUp() {
        paginationEntityMapper = Mappers.getMapper(PaginationEntityMapper.class);
    }

    @Nested
    @DisplayName("toPageable() mapping tests")
    class ToPageableTests {

        @Test
        @DisplayName("Should convert PaginationQuery to Pageable with ASC sort")
        void shouldConvertToPageableWithAscSort() {
            // Arrange
            PaginationQuery query = new PaginationQuery(0, 10, "id", "ASC");

            // Act
            Pageable result = paginationEntityMapper.toPageable(query);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getPageNumber());
            assertEquals(10, result.getPageSize());
            assertTrue(result.getSort().isSorted());
            assertEquals(Sort.Direction.ASC, result.getSort().getOrderFor("id").getDirection());
        }

        @Test
        @DisplayName("Should convert PaginationQuery to Pageable with DESC sort")
        void shouldConvertToPageableWithDescSort() {
            // Arrange
            PaginationQuery query = new PaginationQuery(0, 10, "title", "DESC");

            // Act
            Pageable result = paginationEntityMapper.toPageable(query);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getPageNumber());
            assertEquals(10, result.getPageSize());
            assertTrue(result.getSort().isSorted());
            assertEquals(Sort.Direction.DESC, result.getSort().getOrderFor("title").getDirection());
        }

        @Test
        @DisplayName("Should handle case-insensitive DESC direction")
        void shouldHandleCaseInsensitiveDesc() {
            // Arrange
            PaginationQuery query1 = new PaginationQuery(0, 10, "id", "desc");
            PaginationQuery query2 = new PaginationQuery(0, 10, "id", "Desc");
            PaginationQuery query3 = new PaginationQuery(0, 10, "id", "DESC");

            // Act
            Pageable result1 = paginationEntityMapper.toPageable(query1);
            Pageable result2 = paginationEntityMapper.toPageable(query2);
            Pageable result3 = paginationEntityMapper.toPageable(query3);

            // Assert
            assertEquals(Sort.Direction.DESC, result1.getSort().getOrderFor("id").getDirection());
            assertEquals(Sort.Direction.DESC, result2.getSort().getOrderFor("id").getDirection());
            assertEquals(Sort.Direction.DESC, result3.getSort().getOrderFor("id").getDirection());
        }

        @Test
        @DisplayName("Should default to ASC for invalid sort direction")
        void shouldDefaultToAscForInvalidDirection() {
            // Arrange
            PaginationQuery query = new PaginationQuery(0, 10, "id", "INVALID");

            // Act
            Pageable result = paginationEntityMapper.toPageable(query);

            // Assert
            assertEquals(Sort.Direction.ASC, result.getSort().getOrderFor("id").getDirection());
        }

        @Test
        @DisplayName("Should handle different page numbers")
        void shouldHandleDifferentPageNumbers() {
            // Arrange
            PaginationQuery query1 = new PaginationQuery(0, 10, "id", "ASC");
            PaginationQuery query2 = new PaginationQuery(5, 10, "id", "ASC");
            PaginationQuery query3 = new PaginationQuery(10, 10, "id", "ASC");

            // Act
            Pageable result1 = paginationEntityMapper.toPageable(query1);
            Pageable result2 = paginationEntityMapper.toPageable(query2);
            Pageable result3 = paginationEntityMapper.toPageable(query3);

            // Assert
            assertEquals(0, result1.getPageNumber());
            assertEquals(5, result2.getPageNumber());
            assertEquals(10, result3.getPageNumber());
        }

        @Test
        @DisplayName("Should handle different page sizes")
        void shouldHandleDifferentPageSizes() {
            // Arrange
            PaginationQuery query1 = new PaginationQuery(0, 10, "id", "ASC");
            PaginationQuery query2 = new PaginationQuery(0, 20, "id", "ASC");
            PaginationQuery query3 = new PaginationQuery(0, 50, "id", "ASC");

            // Act
            Pageable result1 = paginationEntityMapper.toPageable(query1);
            Pageable result2 = paginationEntityMapper.toPageable(query2);
            Pageable result3 = paginationEntityMapper.toPageable(query3);

            // Assert
            assertEquals(10, result1.getPageSize());
            assertEquals(20, result2.getPageSize());
            assertEquals(50, result3.getPageSize());
        }

        @Test
        @DisplayName("Should handle different sort fields")
        void shouldHandleDifferentSortFields() {
            // Arrange
            PaginationQuery queryById = new PaginationQuery(0, 10, "id", "ASC");
            PaginationQuery queryByTitle = new PaginationQuery(0, 10, "title", "ASC");
            PaginationQuery queryByAuthor = new PaginationQuery(0, 10, "author", "ASC");

            // Act
            Pageable resultId = paginationEntityMapper.toPageable(queryById);
            Pageable resultTitle = paginationEntityMapper.toPageable(queryByTitle);
            Pageable resultAuthor = paginationEntityMapper.toPageable(queryByAuthor);

            // Assert
            assertNotNull(resultId.getSort().getOrderFor("id"));
            assertNotNull(resultTitle.getSort().getOrderFor("title"));
            assertNotNull(resultAuthor.getSort().getOrderFor("author"));
        }

        @Test
        @DisplayName("Should create Sort with correct direction and property")
        void shouldCreateSortCorrectly() {
            // Arrange
            PaginationQuery query = new PaginationQuery(2, 20, "createdAt", "DESC");

            // Act
            Pageable result = paginationEntityMapper.toPageable(query);

            // Assert
            Sort sort = result.getSort();
            assertTrue(sort.isSorted());
            Sort.Order order = sort.getOrderFor("createdAt");
            assertNotNull(order);
            assertEquals(Sort.Direction.DESC, order.getDirection());
            assertEquals("createdAt", order.getProperty());
        }

        @Test
        @DisplayName("Should handle null sort direction as ASC")
        void shouldHandleNullSortDirection() {
            // Arrange
            PaginationQuery query = new PaginationQuery(0, 10, "id", null);

            // Act
            Pageable result = paginationEntityMapper.toPageable(query);

            // Assert
            assertEquals(Sort.Direction.ASC, result.getSort().getOrderFor("id").getDirection());
        }

        @Test
        @DisplayName("Should handle empty sort direction as ASC")
        void shouldHandleEmptySortDirection() {
            // Arrange
            PaginationQuery query = new PaginationQuery(0, 10, "id", "");

            // Act
            Pageable result = paginationEntityMapper.toPageable(query);

            // Assert
            assertEquals(Sort.Direction.ASC, result.getSort().getOrderFor("id").getDirection());
        }

        @Test
        @DisplayName("Should create PageRequest with all parameters")
        void shouldCreatePageRequestWithAllParameters() {
            // Arrange
            PaginationQuery query = new PaginationQuery(3, 15, "publicationYear", "DESC");

            // Act
            Pageable result = paginationEntityMapper.toPageable(query);

            // Assert
            assertEquals(3, result.getPageNumber());
            assertEquals(15, result.getPageSize());
            assertEquals(Sort.Direction.DESC, result.getSort().getOrderFor("publicationYear").getDirection());
            assertEquals("publicationYear", result.getSort().iterator().next().getProperty());
        }

        @Test
        @DisplayName("Should handle first page correctly")
        void shouldHandleFirstPage() {
            // Arrange
            PaginationQuery query = new PaginationQuery(0, 10, "id", "ASC");

            // Act
            Pageable result = paginationEntityMapper.toPageable(query);

            // Assert
            assertEquals(0, result.getPageNumber());
            assertTrue(result.isPaged());
        }

        @Test
        @DisplayName("Should create valid Pageable for pagination calculations")
        void shouldCreateValidPageableForPagination() {
            // Arrange
            PaginationQuery query = new PaginationQuery(2, 10, "id", "ASC");

            // Act
            Pageable result = paginationEntityMapper.toPageable(query);

            // Assert
            assertEquals(20, result.getOffset()); // page 2 with size 10 = offset 20
            assertTrue(result.isPaged());
        }

        @Test
        @DisplayName("Should handle asc in lowercase")
        void shouldHandleAscInLowercase() {
            // Arrange
            PaginationQuery query = new PaginationQuery(0, 10, "id", "asc");

            // Act
            Pageable result = paginationEntityMapper.toPageable(query);

            // Assert
            assertEquals(Sort.Direction.ASC, result.getSort().getOrderFor("id").getDirection());
        }

        @Test
        @DisplayName("Should handle mixed case sort directions")
        void shouldHandleMixedCaseSortDirections() {
            // Arrange
            PaginationQuery query1 = new PaginationQuery(0, 10, "id", "AsC");
            PaginationQuery query2 = new PaginationQuery(0, 10, "id", "DeSc");

            // Act
            Pageable result1 = paginationEntityMapper.toPageable(query1);
            Pageable result2 = paginationEntityMapper.toPageable(query2);

            // Assert
            assertEquals(Sort.Direction.ASC, result1.getSort().getOrderFor("id").getDirection());
            assertEquals(Sort.Direction.DESC, result2.getSort().getOrderFor("id").getDirection());
        }
    }
}
