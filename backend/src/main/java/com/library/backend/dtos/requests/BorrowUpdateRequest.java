package com.library.backend.dtos.requests;

import com.library.backend.entities.Borrow;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowUpdateRequest {

    LocalDate expectedReturnDate;
    Borrow.Type status;

}
