package support.test;

import nextstep.domain.User;
import org.springframework.http.*;

public abstract class ApiAcceptanceTest extends AcceptanceTest {
    public static <T> HttpEntity createHttpEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    protected String createResource(String path, User loginUser, Object bodyPayload) {
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(path, bodyPayload, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getHeaders().getLocation().getPath();
    }

    protected String createResource(String path, Object bodyPayload) {
        ResponseEntity<String> response = template().postForEntity(path, bodyPayload, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getHeaders().getLocation().getPath();
    }

    protected <T> T getResource(String location, User loginUser, Class<T> responseType) {
        return basicAuthTemplate(loginUser).getForObject(location, responseType);
    }

    protected <T> T getResource(String location, Class<T> responseType) {
        return template().getForObject(location, responseType);
    }

    protected ResponseEntity<Void> deleteResourceResponseEntity(String path, User loginUser) {
        return basicAuthTemplate(loginUser).exchange(path, HttpMethod.DELETE, null, Void.class);
    }

    protected <T> ResponseEntity<T> modifyResourceResponseEntity(String path, User loginUser, Object bodyPayload, Class<T> responseType) {
        return basicAuthTemplate(loginUser).exchange(path, HttpMethod.PUT, createHttpEntity(bodyPayload), responseType);
    }

    protected <T> ResponseEntity<T> modifyResourceResponseEntity(String path, Object bodyPayload, Class<T> responseType) {
        return template().exchange(path, HttpMethod.PUT, createHttpEntity(bodyPayload), responseType);
    }

}
