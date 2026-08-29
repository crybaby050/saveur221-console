package com.saveur221.exceptions;

public class CompteDesactiveException extends RuntimeException {
    public CompteDesactiveException(String message) {
        super(message);
    }
}