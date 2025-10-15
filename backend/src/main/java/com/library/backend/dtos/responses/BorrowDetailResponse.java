package com.library.backend.dtos.responses;

import com.library.backend.entities.Borrow;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowDetailResponse {

    Integer id;
    StudentDetailResponse student;
    BookDetailResponse book;
    LocalDate borrowDate;
    LocalDate expectedReturnDate;
    Borrow.Type status;

}
