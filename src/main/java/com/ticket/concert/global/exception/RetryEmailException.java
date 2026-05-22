package com.ticket.concert.global.exception;

public class RetryEmailException extends RuntimeException {
    public RetryEmailException(Throwable cause) {
        super(cause);
    }
}
