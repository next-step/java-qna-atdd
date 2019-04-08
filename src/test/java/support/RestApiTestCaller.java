package support;

import nextstep.domain.User;
import nextstep.dto.ListResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

public class RestApiTestCaller {
    private TestRestTemplate template;

    public RestApiTestCaller(TestRestTemplate template) {
        this.template = template;
    }

    public <T> ResponseEntity<ListResponse<T>> getListResource(String location, Class<T> responseType, User loginUser) {
        return basicAuthTemplate(loginUser).exchange(
            location, HttpMethod.GET,
            null, new ParameterizedTypeReference<ListResponse<T>>() {});
    }

    public <T> ResponseEntity<T> getResource(String url, Class<T> responseType, User loginUser) {
        return basicAuthTemplate(loginUser).getForEntity(url, responseType);
    }

    public <T> ResponseEntity<Void> createResource(String url, T bodyPayload, User loginUser) {
        return basicAuthTemplate(loginUser).postForEntity(url, bodyPayload, Void.class);
    }

    public <P, T> ResponseEntity<T> updateResource(String url, P bodyPayload, Class<T> responseType, User loginUser) {
        return basicAuthTemplate(loginUser).exchange(url,
            HttpMethod.PUT,
            createHttpEntity(bodyPayload),
            responseType);
    }

    public ResponseEntity<Void> deleteResource(String url, User loginUser) {
        return basicAuthTemplate(loginUser).exchange(url,
            HttpMethod.DELETE,
            null,
            Void.class);
    }

    private TestRestTemplate basicAuthTemplate(User loginUser) {
        return template.withBasicAuth(loginUser.getUserId(), loginUser.getPassword());
    }

    private <P> HttpEntity createHttpEntity(P body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
