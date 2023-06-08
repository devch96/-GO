package com.hansung.capstone;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Already Exists")
public class DataExistException extends RuntimeException{
    private static final long serialVersionUID=1L;
    public DataExistException(String message) {
        super(message);
    }
}
