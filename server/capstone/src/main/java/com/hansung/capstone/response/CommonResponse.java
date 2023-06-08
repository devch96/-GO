package com.hansung.capstone.response;

import lombok.Getter;

@Getter
public class CommonResponse {
    int code;
    boolean success;
    String message;
}
