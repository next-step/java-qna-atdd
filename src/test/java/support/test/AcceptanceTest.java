package support.test;

import nextstep.domain.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest extends BaseTest {
    private static final String DEFAULT_LOGIN_USER = "javajigi";

    @Autowired
    private TestRestTemplate template;

    @Autowired private UserRepository userRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private AnswerRepository answerRepository;

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

    protected Question defaultQuestion() {
        return questionRepository.findById(1L).get();
    }

    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).get();
    }

    protected <T> ResponseEntity<T> createResource(User loginUser, String url, Object resource, Class<T> type) {
        return basicAuthTemplate(loginUser).postForEntity(url, resource, type);
    }

    protected <T> ResponseEntity<T> createResourceWithoutLogin(String url, Object resource, Class<T> type) {
        return template().postForEntity(url, resource, type);
    }

    protected <T> ResponseEntity<T> getResource(User loginUser, String url, Class<T> type) {
        return basicAuthTemplate(loginUser).getForEntity(url, type);
    }

    protected <T> ResponseEntity<T> getResourceWithoutLogin(String url, Class<T> type) {
        return template().getForEntity(url, type);
    }

    protected <T> ResponseEntity<T> updateResource(User loginUser, String location, T body, Class<T> type) {
        return basicAuthTemplate(loginUser).exchange(location, HttpMethod.PUT, createHttpEntity(body), type);
    }

    protected <T> ResponseEntity<T> updateResourceWithoutLogin(String location, Object body, Class<T> type) {
        return template().exchange(location, HttpMethod.PUT, createHttpEntity(body), type);
    }

    protected <T> ResponseEntity<T> deleteResource(User loginUser, String location, Class<T> type) {
        return basicAuthTemplate(loginUser).exchange(location, HttpMethod.DELETE, null, type);
    }

    protected <T> ResponseEntity<T> deleteResourceWithoutLogin(String location, Class<T> type) {
        return template().exchange(location, HttpMethod.DELETE, null, type);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
