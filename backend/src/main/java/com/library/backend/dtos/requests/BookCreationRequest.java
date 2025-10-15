package com.library.backend.dtos.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookCreationRequest {

    String title;
    String author;
    String category;
    String publisher;
    String yearPublished;
    Integer availableCopies;
    String description;
    String coverImageUrl;

}
