package com.example.oil_mart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int page;            // 1-based page index (for frontend)
    private int size;            // page size
    private long totalElements;  // total rows in DB
    private int totalPages;      // total pages
    private boolean last;        // is last page
}
