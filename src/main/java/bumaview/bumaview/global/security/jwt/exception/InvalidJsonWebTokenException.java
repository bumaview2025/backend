package bumaview.bumaview.global.security.jwt.exception;

import bumaview.bumaview.global.exception.BumaviewException;
import org.springframework.http.HttpStatus;

public class InvalidJsonWebTokenException extends BumaviewException {
    public InvalidJsonWebTokenException() {
        super(HttpStatus.UNAUTHORIZED, "JWT 토큰이 잘못되었습니다.");
    }
}
