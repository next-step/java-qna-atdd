package support.test;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

/**
 * Created by hspark on 24/11/2018.
 */
public class RestApiExecutor<T> {
	private Class<T> clazz;
	private TestRestTemplate testRestTemplate;
	private HttpMethod httpMethod;
	private Object object;
	private String url;

	private RestApiExecutor(TestRestTemplate testRestTemplate, Class<T> clazz) {
		this.testRestTemplate = testRestTemplate;
		this.clazz = clazz;
	}

	public RestApiExecutor url(String url) {
		this.url = url;
		return this;
	}

	public RestApiExecutor request(Object request) {
		this.object = request;
		return this;
	}

	public RestApiExecutor post() {
		this.httpMethod = HttpMethod.POST;
		return this;
	}

	public RestApiExecutor put() {
		this.httpMethod = HttpMethod.PUT;
		return this;
	}

	public RestApiExecutor delete() {
		this.httpMethod = HttpMethod.DELETE;
		return this;
	}

	public RestApiResult<T> execute() {
		return new RestApiResult<>(testRestTemplate.exchange(url, httpMethod, createHttpEntity(object), clazz));
	}

	public static <T> RestApiExecutor ready(TestRestTemplate testRestTemplate, Class<T> clazz) {
		return new RestApiExecutor<>(testRestTemplate, clazz);
	}

	private HttpEntity createHttpEntity(Object body) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new HttpEntity(body, headers);
	}
}
