package com.saveur221.exceptions;

public class ClientInexistantException extends RuntimeException {
    public ClientInexistantException(String message) {
        super(message);
    }
}