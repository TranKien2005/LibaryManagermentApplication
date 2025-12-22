package com.library.backend.configurations;

import lombok.Data;

import java.io.Serializable;

@Data
public class MyMsg implements Serializable {

    private String id;
    private String content;

}
