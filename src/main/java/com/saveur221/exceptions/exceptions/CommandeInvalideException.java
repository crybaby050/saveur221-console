package com.saveur221.exceptions;

public class CommandeInvalideException extends RuntimeException {
    public CommandeInvalideException(String message) {
        super(message);
    }
}