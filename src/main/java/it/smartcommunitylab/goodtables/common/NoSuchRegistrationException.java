package it.smartcommunitylab.goodtables.common;

public class NoSuchRegistrationException extends Exception {

    private static final long serialVersionUID = 3336889083477209835L;

    public NoSuchRegistrationException() {
        super("no such registration");
    }

    public NoSuchRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchRegistrationException(String message) {
        super(message);
    }

    public NoSuchRegistrationException(Throwable cause) {
        super(cause);
    }
}
