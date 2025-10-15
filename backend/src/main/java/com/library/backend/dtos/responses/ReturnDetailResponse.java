package com.library.backend.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReturnDetailResponse {

    Integer id;
    BorrowDetailResponse borrow;
    LocalDate returnDate;
    Integer damagePercentage;

}
