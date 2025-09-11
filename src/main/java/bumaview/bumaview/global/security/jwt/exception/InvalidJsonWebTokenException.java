package bumaview.bumaview.global.security.jwt.exception;

public class InvalidJsonWebTokenException extends RuntimeException {
    public InvalidJsonWebTokenException(String message) {
        super(message);
    }
}
