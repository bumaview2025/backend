package bumaview.bumaview.domain.question.exception;

import bumaview.bumaview.global.exception.BumaviewException;
import org.springframework.http.HttpStatus;

public class InvalidQuestionException extends BumaviewException {
    public InvalidQuestionException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}