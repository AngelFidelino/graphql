package com.aflr.graphqlspring.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException() {
        super();
    }
    public NotFoundException(String message) {
        super(message);
    }
}
