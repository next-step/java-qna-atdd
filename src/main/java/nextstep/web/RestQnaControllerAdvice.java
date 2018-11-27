package nextstep.web;

import nextstep.AlreadyDeletedException;
import nextstep.CannotDeleteException;
import nextstep.NotFoundException;
import nextstep.security.RestSecurityControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = {ApiQuestionController.class, ApiAnswerController.class})
public class RestQnaControllerAdvice {
	private static final Logger log = LoggerFactory.getLogger(RestSecurityControllerAdvice.class);

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void handleException(NotFoundException exception) {
		log.debug(exception.getMessage());
	}

	@ExceptionHandler(AlreadyDeletedException.class)
	@ResponseStatus(HttpStatus.GONE)
	public void handleAlreadyDeletedException(AlreadyDeletedException exception) {
		log.debug(exception.getMessage());
	}

	@ExceptionHandler(CannotDeleteException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public void handleCannotDeleteException(CannotDeleteException exception) {
		log.debug(exception.getMessage());
	}
}
