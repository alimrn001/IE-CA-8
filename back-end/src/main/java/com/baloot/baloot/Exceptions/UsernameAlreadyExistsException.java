package com.baloot.baloot.Exceptions;

public class UsernameAlreadyExistsException extends Exception {
    public UsernameAlreadyExistsException() {
        super("This username already exists");
    }
}
