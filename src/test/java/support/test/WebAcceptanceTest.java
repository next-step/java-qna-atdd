package support.test;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import static java.util.Arrays.asList;

public abstract class WebAcceptanceTest extends AcceptanceTest {
    public HttpEntity createWebRequestEntity(MultiValueMap<String, Object> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return new HttpEntity<>(params, headers);
    }

    public HttpEntity createWebRequestEntity() {
        return createWebRequestEntity(null);
    }


    public String getResponseLocationPath(ResponseEntity<String> response) {
        return response.getHeaders().getLocation().getPath();
    }
}
