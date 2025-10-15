package com.library.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "books")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String title;

    String author;

    String category;

    String publisher;

    Integer yearPublished;

    Integer availableCopies = 0;

    String description;

    Double rating = 0.0;

    Integer reviewCount = 0;

    String coverImageUrl;

}
