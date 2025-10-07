package bumaview.bumaview.domain.interview.exception;

import bumaview.bumaview.global.exception.BumaviewException;
import org.springframework.http.HttpStatus;

public class PortfolioRequiredException extends BumaviewException {
    public PortfolioRequiredException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}