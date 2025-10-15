package com.library.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "borrows")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Borrow {

    public enum Type {
        Borrowed,
        Returned
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    Student student;

    @ManyToOne
    @JoinColumn(name = "bookId", nullable = false)
    Book book;

    LocalDate borrowDate;

    LocalDate expectedReturnDate;

    @Enumerated(EnumType.STRING)
    Type status;
}
