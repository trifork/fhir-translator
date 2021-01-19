package com.trifork.ehealth.translation.exceptions;

public class TranslatorException extends RuntimeException {

    public TranslatorException() {
    }

    public TranslatorException(String message) {
        super(message);
    }

    public TranslatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public TranslatorException(Throwable cause) {
        super(cause);
    }

    public TranslatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
