package support.test;

import nextstep.domain.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest extends BaseTest {
    private static final String DEFAULT_LOGIN_USER = "javajigi";
    private static final String OTHER_LOGIN_USER = "sanjigi";
    private static final long DEFAULT_QUESTION_ID = 1L;
    private static final long DEFAULT_ANSWER_ID = 1L;

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

    protected User otherUser() {
        return findByUserId(OTHER_LOGIN_USER);
    }

    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
    }

    protected Question defaultQuestion() {
        return findById(DEFAULT_QUESTION_ID);
    }

    protected Question findById(long id) {
        return questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    protected Answer defaultAnswer() {
        return findByAnswerId(DEFAULT_ANSWER_ID);
    }

    protected Answer findByAnswerId(long id) {
        return answerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    protected String createResourceLocation(String path, Object bodyPayload) {
        ResponseEntity<String> response = template().postForEntity(path, bodyPayload, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getHeaders().getLocation().getPath();
    }

    protected String createLoginResourceLocation(User loginUser, String path, Object bodyPayload) {
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(path, bodyPayload, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getHeaders().getLocation().getPath();
    }

    protected <T> T getResource(String location, Class<T> responseType, User loginUser) {
        return basicAuthTemplate(loginUser).getForObject(location, responseType);
    }

    protected <T> ResponseEntity<T> putLoginResponseEntity(String location, Class<T> responseType, User loginUser, Object updateObject) {
        return basicAuthTemplate(loginUser).exchange(location, HttpMethod.PUT, createHttpEntity(updateObject), responseType);
    }

    protected <T> ResponseEntity<T> putResponseEntity(String location, Class<T> responseType, Object updateObject) {
        return template().exchange(location, HttpMethod.PUT, createHttpEntity(updateObject), responseType);
    }

    protected <T> ResponseEntity<T> deleteLoginResponseEntity(String location, Class<T> responseType, User loginUser, Object deleteObject) {
        return basicAuthTemplate(loginUser).exchange(location, HttpMethod.DELETE, createHttpEntity(deleteObject), responseType);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
