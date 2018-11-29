package support.test;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.domain.UserRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest extends BaseTest {
    private static final String DEFAULT_LOGIN_USER = "javajigi";

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private UserRepository userRepository;

    public TestRestTemplate template() {
        return template;
    }

    public TestRestTemplate basicAuthTemplate() {
        return basicAuthTemplate(defaultUser());
    }

    public TestRestTemplate basicAuthTemplate(User loginUser) {
        return template.withBasicAuth(loginUser.getUserId(), loginUser.getPassword());
    }

    protected User defaultUser() {
        return findByUserId(DEFAULT_LOGIN_USER);
    }

    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).get();
    }

    protected String createResource(String path, Object bodyPayload) {
        ResponseEntity<String> response = template().postForEntity(path, bodyPayload, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getHeaders().getLocation().getPath();
    }

    protected <T> T getResource(URI location, Class<T> responseType, User loginUser) {
        return basicAuthTemplate(loginUser).getForObject(location, responseType);
    }

    protected ResponseEntity getExchange(URI location, HttpMethod httpMethod, HttpEntity httpEntity, Class classType) {
        return basicAuthTemplate(defaultUser()).exchange(location, httpMethod, httpEntity, classType);
    }

    protected ResponseEntity getExchangeNotLogin(URI location, HttpMethod httpMethod, HttpEntity httpEntity, Class classType) {
        return template().exchange(location, httpMethod, httpEntity, classType);
    }

    protected ResponseEntity getExchangeUser(User user,URI location, HttpMethod httpMethod, HttpEntity httpEntity, Class classType) {
        return basicAuthTemplate(user).exchange(location, httpMethod, httpEntity, classType);
    }

    protected HttpEntity createHttp(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }

    protected HttpEntity emptyHttp() {
        HttpHeaders headers = new HttpHeaders();
        return new HttpEntity(headers);
    }


}
