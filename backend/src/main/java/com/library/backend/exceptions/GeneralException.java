package com.library.backend.exceptions;

import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private final ResponseCode code;

    public GeneralException(ResponseCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public GeneralException(ResponseCode code, Throwable cause) {
        super(code.getMessage(), cause);
        this.code = code;
    }

}
