package com.library.backend.dtos.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookUpdateRequest {
    String title;
    String author;
    String category;
    String publisher;
    Integer yearPublished;
    Integer availableCopies;
    String coverImageUrl;
    String description;
}
