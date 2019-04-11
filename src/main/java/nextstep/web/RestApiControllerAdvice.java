package nextstep.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice(annotations = RestController.class)
public class RestApiControllerAdvice {
    private static final Logger log = LoggerFactory.getLogger(RestApiControllerAdvice.class);

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void illegalArgumentException(IllegalArgumentException e) {
        log.debug("JSON API IllegalArgumentException is happened!");
    }

    @ExceptionHandler(EntityNotFoundException .class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void entityNotFoundException(EntityNotFoundException e) {
        log.debug("JSON API EntityNotFoundException is happened!");
    }
}
