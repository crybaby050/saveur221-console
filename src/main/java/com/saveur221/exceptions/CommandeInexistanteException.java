package com.saveur221.exceptions;

public class CommandeInexistanteException extends RuntimeException {
    public CommandeInexistanteException(String message) {
        super(message);
    }
}