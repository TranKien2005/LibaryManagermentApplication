package com.library.backend.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ManagerDetailResponse {
    Integer id;
    String fullName;
    String phone;
    String email;
    String username;
    String password;
}
