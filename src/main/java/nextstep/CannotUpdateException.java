package nextstep;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class CannotUpdateException extends Exception {
    private static final long serialVersionUID = 1L;

    public CannotUpdateException(String message) {
        super(message);
    }
}
