package lemon.challenge.exceptions;

import org.springframework.http.HttpStatus;

public class ExternalServiceException extends RuntimeException{

    private HttpStatus status;

    public ExternalServiceException(HttpStatus status) {
        this.status = status;
    }

    public ExternalServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ExternalServiceException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public ExternalServiceException(Throwable cause, HttpStatus status) {
        super(cause);
        this.status = status;
    }

    public ExternalServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatus status) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
