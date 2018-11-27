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
    private static final long DEFAULT_QUESTION_ID = 1;
    private static final long DEFAULT_ANSWER_ID = 1;

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

    protected Question defaultQuestion() {
        return findByQuestionId(DEFAULT_QUESTION_ID);
    }

    protected Answer defaultAnswer() {
        return findByAnswerId(DEFAULT_ANSWER_ID);
    }

    protected Question findByQuestionId(Long questionId) {
        return questionRepository.findById(questionId).get();
    }

    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).get();
    }

    protected Answer findByAnswerId(long answerId) {
        return answerRepository.findById(answerId).get();
    }

    protected String createResource(String path, Object bodyPayload) {
        ResponseEntity<String> response = template.postForEntity(path, bodyPayload, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getHeaders().getLocation().getPath();
    }

    protected String createResource(String path, Object bodyPayload, User loginUser) {
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(path, bodyPayload, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getHeaders().getLocation().getPath();
    }

    protected <T> T getResource(String location, Class<T> responseType, User loginUser) {
        return basicAuthTemplate(loginUser).getForObject(location, responseType);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
    protected <T> ResponseEntity<T> getExchange(TestRestTemplate testRestTemplate, String location, Object body, Class<T> responseType) {
        return testRestTemplate.exchange(location, HttpMethod.PUT, createHttpEntity(body), responseType);
    }

    protected <T> ResponseEntity<T> getExchange(String location, Object body, User loginUser, Class<T> responseType) {
        return getExchange(basicAuthTemplate(loginUser), location, body, responseType);
    }
}
