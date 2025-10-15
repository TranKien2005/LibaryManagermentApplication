package com.library.backend.dtos.requests.v1;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRequest {
    int borrowID;
    int accountID;
    int bookID;
    LocalDate borrowDate;
    LocalDate expectedReturnDate;
    String status;
}
