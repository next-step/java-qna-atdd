package support.test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by hspark on 27/11/2018.
 */
public class RestApiResult<T> {
	private ResponseEntity<T> responseEntity;

	public String getResourceLocation() {
		return responseEntity.getHeaders().getLocation().getPath();
	}

	public T getBody() {
		return responseEntity.getBody();
	}

	public RestApiResult(ResponseEntity<T> responseEntity) {
		this.responseEntity = responseEntity;
	}

	public HttpStatus getStatusCode() {
		return responseEntity.getStatusCode();
	}
}
