package com.billings.middlewareservice.exceptions;

public class IllegalArgumentException extends RuntimeException{

    public IllegalArgumentException(String message) {
        super(message);
    }
}
