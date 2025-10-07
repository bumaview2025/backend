package bumaview.bumaview.domain.interview.exception;

import bumaview.bumaview.global.exception.BumaviewException;
import org.springframework.http.HttpStatus;

public class InterviewNotFoundException extends BumaviewException {
    public InterviewNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}