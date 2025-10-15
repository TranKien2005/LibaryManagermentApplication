package com.library.backend.dtos.requests.v1;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.InputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Document {
    String title;
    String author;
    String category;
    String publisher;
    int yearPublished;
    int availableCopies;
    int bookID;
    String description;
    double rating;
    int reviewCount;
    InputStream coverImage;
}
