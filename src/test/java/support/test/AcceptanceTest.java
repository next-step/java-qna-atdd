package support.test;

import nextstep.domain.AnswerRepository;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.domain.UserRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest extends BaseTest {
    private static final String DEFAULT_LOGIN_USER = "javajigi";

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

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

    protected HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }

    protected HttpEntity createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(headers);
    }

    protected String createResource(TestRestTemplate template, String path, Object bodyPayload) {
        ResponseEntity<String> response = template.postForEntity(path, bodyPayload, String.class);
        assertThat(response.getStatusCode()).isEqualTo((HttpStatus.CREATED));
        return response.getHeaders().getLocation().getPath();
    }

    protected <T> ResponseEntity<T> updateResource(TestRestTemplate template, String location, Object bodyPayload, Class<T> responseType) {
        return template.exchange(location, HttpMethod.PUT, createHttpEntity(bodyPayload), responseType);
    }

    protected <T> ResponseEntity<T> deleteResource(TestRestTemplate template, String location, Class<T> responseType) {
        return template.exchange(location, HttpMethod.DELETE, createHttpEntity(), responseType);
    }

    protected <T> T getResource(TestRestTemplate template, String location, Class<T> responseType) {
        return template.getForObject(location, responseType);
    }
}
