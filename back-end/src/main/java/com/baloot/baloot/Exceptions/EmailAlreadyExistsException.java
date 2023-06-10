package com.baloot.baloot.Exceptions;

public class EmailAlreadyExistsException extends Exception {
    public EmailAlreadyExistsException() {
        super("This Email already exists");
    }
}
