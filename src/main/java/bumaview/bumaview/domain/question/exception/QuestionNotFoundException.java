package bumaview.bumaview.domain.question.exception;

import bumaview.bumaview.global.exception.BumaviewException;
import org.springframework.http.HttpStatus;

public class QuestionNotFoundException extends BumaviewException {
    public QuestionNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}