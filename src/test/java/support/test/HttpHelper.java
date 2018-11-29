package support.test;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;

public class HttpHelper {

    public static ResponseEntity<String> get(TestRestTemplate template, String url) {
        return template.getForEntity(url, String.class);
    }

    public static ResponseEntity<String> post(TestRestTemplate template,
                                              String url,
                                              MultiValueMap<String, Object> params) {
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, getHeaders());

        return template.postForEntity(url, request, String.class);
    }

    public static ResponseEntity<String> put(TestRestTemplate template,
                                             String url,
                                             MultiValueMap<String, Object> params) {
        params.add("_method", "put");

        return post(template, url, params);
    }

    public static ResponseEntity<String> delete(TestRestTemplate template,
                                                String url) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("_method", "delete");

        return post(template, url, params);
    }

    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return headers;
    }
}
