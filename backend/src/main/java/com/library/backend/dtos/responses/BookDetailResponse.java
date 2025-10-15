package com.library.backend.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookDetailResponse {
    Integer id;
    String title;
    String author;
    String category;
    String publisher;
    Integer yearPublished;
    Integer availableCopies;
    String description;
    Double rating;
    Integer reviewCount;
    String coverImageUrl;
}
