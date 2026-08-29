package com.saveur221.exceptions;

public class UtilisateurInexistantException extends RuntimeException {
    public UtilisateurInexistantException(String message) {
        super(message);
    }
}