package it.smartcommunitylab.goodtables.minio;

public class MinioException extends Exception {

    private static final long serialVersionUID = 6798121311546888567L;

    public MinioException() {
        super();
    }

    public MinioException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinioException(String message) {
        super(message);
    }

    public MinioException(Throwable cause) {
        super(cause);
    }
}
