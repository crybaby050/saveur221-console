package com.saveur221.exceptions;

public class MontantPaiementInvalideException extends RuntimeException {
    public MontantPaiementInvalideException(String message) {
        super(message);
    }
}