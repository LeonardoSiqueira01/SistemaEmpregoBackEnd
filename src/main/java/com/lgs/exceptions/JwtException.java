package com.lgs.exceptions;


public class JwtException extends RuntimeException {

    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
