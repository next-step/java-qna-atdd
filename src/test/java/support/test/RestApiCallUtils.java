package support.test;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

public class RestApiCallUtils {

    public static <R> ResponseEntity<Void> createResource(TestRestTemplate template, String url, R requestBody) {
        HttpEntity<R> request = RestApiCallUtils.createHttpEntity(requestBody);
        return template.exchange(url, HttpMethod.POST, request, Void.class);
    }

    public static <R> ResponseEntity<List<R>> getListResource(TestRestTemplate template, String url, Class<R> responseType) {
        return template.exchange(url, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<R>>() {});
    }

    public static <R> ResponseEntity<R> getResource(TestRestTemplate template, String url, Class<R> responseType) {
        return template.getForEntity(url, responseType);
    }

    public static <T, R> ResponseEntity<R> updateResource(TestRestTemplate template, String url, T body, Class<R> responseType) {
        return template.exchange(url, HttpMethod.PUT, createHttpEntity(body), responseType);
    }

    public static ResponseEntity<Void> deleteResource(TestRestTemplate template, String url) {
        return template.exchange(url, HttpMethod.DELETE, null, Void.class);
    }

    public static HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
