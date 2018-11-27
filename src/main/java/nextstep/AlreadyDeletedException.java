package nextstep;

public class AlreadyDeletedException extends RuntimeException {

	public AlreadyDeletedException() {
		super();
	}

	public AlreadyDeletedException(String message) {
		super(message);
	}

	public AlreadyDeletedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlreadyDeletedException(Throwable cause) {
		super(cause);
	}

	protected AlreadyDeletedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
