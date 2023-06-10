package com.baloot.baloot.Exceptions;

public class NotEnoughCreditException extends Exception {
    public NotEnoughCreditException() {
        super("There is not enough credit in your account to make purchase!");
    }
}
