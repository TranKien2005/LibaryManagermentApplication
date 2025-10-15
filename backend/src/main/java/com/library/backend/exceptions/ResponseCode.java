package com.library.backend.exceptions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public enum ResponseCode {

    STUDENT_NOT_FOUND("Student does not exist in the system."),
    BOOK_NOT_FOUND("Book does not exist in the system."),
    BORROW_NOT_FOUND("Borrow does not exist in the system."),
    RETURN_NOT_FOUND("Return does not exist in the system."),
    MANAGER_NOT_FOUND("Manager does not exist in the system."),
    USER_NOT_FOUND("User does not exist in the system."),

    NOT_ENOUGH_BOOK("System doesn't have enough books."),
    UNKNOWN_ERROR("Unknown error.");

    String message;

}
