package com.example.cowmarketplace.exception;

public class CowAlreadySoldException extends RuntimeException {
    public CowAlreadySoldException(String message) {
        super(message);
    }
}
