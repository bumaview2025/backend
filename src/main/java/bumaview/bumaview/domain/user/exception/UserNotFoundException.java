package bumaview.bumaview.domain.user.exception;

import bumaview.bumaview.global.exception.BumaviewException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BumaviewException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "해당 유저를 조회할 수 없습니다.");
    }
}
