package com.demo.MoneyMap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DTO for paginated responses.
 *
 * @param <T> The type of items in the page
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Paginated response wrapper")
public class PagedResponseDTO<T> {

    @Schema(description = "List of items in the current page")
    private List<T> content;

    @Schema(description = "Current page number (0-indexed)", example = "0")
    private int pageNumber;

    @Schema(description = "Number of items per page", example = "10")
    private int pageSize;

    @Schema(description = "Total number of elements across all pages", example = "35")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "4")
    private int totalPages;

    @Schema(description = "Is this the first page", example = "true")
    private boolean first;

    @Schema(description = "Is this the last page", example = "false")
    private boolean last;

    @Schema(description = "Is the content empty", example = "false")
    private boolean empty;

    /**
     * Factory method to convert Spring Data Page to PagedResponseDTO
     */
    public static <T, R> PagedResponseDTO<R> from(Page<T> page, Function<T, R> mapper) {
        return PagedResponseDTO.<R>builder()
                .content(page.getContent().stream().map(mapper).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
