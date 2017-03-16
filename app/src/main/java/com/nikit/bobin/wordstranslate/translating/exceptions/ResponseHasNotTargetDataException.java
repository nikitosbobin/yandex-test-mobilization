package com.nikit.bobin.wordstranslate.translating.exceptions;

public class ResponseHasNotTargetDataException extends Exception {
    public ResponseHasNotTargetDataException(String message) {
        super(message);
    }


    public ResponseHasNotTargetDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
