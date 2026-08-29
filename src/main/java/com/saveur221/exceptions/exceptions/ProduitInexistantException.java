package com.saveur221.exceptions;

public class ProduitInexistantException extends RuntimeException {
    public ProduitInexistantException(String message) {
        super(message);
    }
}