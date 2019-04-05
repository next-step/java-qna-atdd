package support.helper;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ApiExecuteBuilder<T> {
    private TestRestTemplate testRestTemplate;
    private String url;
    private HttpMethod httpMethod;
    private Object body;
    private Class<T> clazz;

    public ApiExecuteBuilder(TestRestTemplate testRestTemplate, Class<T> clazz) {
        this.testRestTemplate = testRestTemplate;
        this.clazz = clazz;
    }

    public static <T> ApiExecuteBuilder setUp(TestRestTemplate testRestTemplate, Class<T> clazz) {
        return new ApiExecuteBuilder<>(testRestTemplate, clazz);
    }

    public ApiExecuteBuilder url(String url) {
        this.url = url;
        return this;
    }

    public ApiExecuteBuilder request(Object body) {
        this.body = body;
        return this;
    }

    public ApiExecuteBuilder post() {
        this.httpMethod = HttpMethod.POST;
        return this;
    }

    public ApiExecuteBuilder put() {
        this.httpMethod = HttpMethod.PUT;
        return this;
    }

    public ApiExecuteBuilder delete() {
        this.httpMethod = HttpMethod.DELETE;
        return this;
    }

    public ResponseEntity<T> execute() {
        return testRestTemplate.exchange(url, httpMethod, createHttpEntity(body), clazz);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
