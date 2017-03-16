package com.nikit.bobin.wordstranslate.translating.exceptions;

public class NotSuccessfulResponseException extends RuntimeException {
    public NotSuccessfulResponseException(String message) {
        super(message);
    }

    public NotSuccessfulResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
