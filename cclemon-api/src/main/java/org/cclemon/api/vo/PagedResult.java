package org.cclemon.api.vo;

import lombok.Builder;
import lombok.Value;
import java.util.List;

/**
 * A framework-agnostic DTO for paginated results.
 *
 * @param <T> the type of the content.
 */
@Value
@Builder
public class PagedResult<T> {

    /**
     * The content of the current page.
     */
    List<T> content;

    /**
     * The current page number (0-based).
     */
    int page;

    /**
     * The number of items per page.
     */
    int size;

    /**
     * The total number of items across all pages.
     */
    long totalElements;

    /**
     * The total number of pages.
     */
    int totalPages;

    /**
     * Whether this is the last page.
     */
    boolean isLast;
}
