package support.test;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

public class HttpClientRequestUtils {

    public static <T> ResponseEntity<T> createResource(TestRestTemplate template, String path, Object bodyPayload, Class<T> responseType) {
        return template.postForEntity(path, bodyPayload, responseType);
    }

    public static <T> T getResource(TestRestTemplate template, String location, Class<T> responseType) {
        return template.getForObject(location, responseType);
    }

    public static <T> ResponseEntity<T> showResource(TestRestTemplate template, String location, Class<T> responseType) {
        return template.getForEntity(location, responseType);
    }

    public static <T> ResponseEntity<T> updateResource(TestRestTemplate template, String location, Object bodyPayload, Class<T> classType) {
        return template.exchange(location, HttpMethod.PUT, createHttpEntity(bodyPayload), classType);
    }

    public static <T> ResponseEntity<T> deleteResource(TestRestTemplate template, String location, Class<T> classType) {
        return template.exchange(location, HttpMethod.DELETE, createHttpEntity(null), classType);
    }

    public static HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
