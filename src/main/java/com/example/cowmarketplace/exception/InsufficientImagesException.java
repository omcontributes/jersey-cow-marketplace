package com.example.cowmarketplace.exception;

public class InsufficientImagesException extends RuntimeException {
    public InsufficientImagesException(String message) {
        super(message);
    }
}