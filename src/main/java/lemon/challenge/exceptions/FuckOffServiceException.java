package lemon.challenge.exceptions;

public class FuckOffServiceException extends RuntimeException{
    public FuckOffServiceException() {
    }

    public FuckOffServiceException(String message) {
        super(message);
    }

    public FuckOffServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FuckOffServiceException(Throwable cause) {
        super(cause);
    }

    public FuckOffServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
