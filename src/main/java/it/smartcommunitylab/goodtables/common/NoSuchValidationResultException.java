package it.smartcommunitylab.goodtables.common;

public class NoSuchValidationResultException extends Exception {

    private static final long serialVersionUID = -4560104844169535255L;

    public NoSuchValidationResultException() {
        super("no such validation result");
    }

    public NoSuchValidationResultException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchValidationResultException(String message) {
        super(message);
    }

    public NoSuchValidationResultException(Throwable cause) {
        super(cause);
    }
}
