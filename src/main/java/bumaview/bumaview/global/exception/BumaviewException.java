package bumaview.bumaview.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BumaviewException extends RuntimeException {
  private final HttpStatus status;

    public BumaviewException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
