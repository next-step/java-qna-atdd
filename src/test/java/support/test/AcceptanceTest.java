package support.test;

import nextstep.domain.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;


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

    protected String foundResource(String path, Object bodyPayload) {
        ResponseEntity<String> response = basicAuthTemplate().postForEntity(path, bodyPayload, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        return response.getHeaders().getLocation().getPath();
    }

    protected String createResource(String path, Object bodyPayload) {
        ResponseEntity<Void> response = template().postForEntity(path, bodyPayload, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return response.getHeaders().getLocation().getPath();
    }

    protected <T> T getResource(String location, Class<T> responseType, User loginUser) {
        return basicAuthTemplate(loginUser).getForObject(location, responseType);
    }

    protected User defaultUser() {
        return findByUserId(DEFAULT_LOGIN_USER);
    }

    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).get();
    }

    protected Optional<Question> defaultQuestion() {
        return questionRepository.findById(DEFAULT_QUESTION_ID);
    }

    protected Optional<Answer> defaultAnswer() {
        return answerRepository.findById(DEFAULT_ANSWER_ID);
    }

    protected String getQuestionPath(String pathValue) {
        return String.format("/questions" + pathValue, defaultQuestion().orElseThrow(IllegalArgumentException::new).getId());
    }

    protected String getAnswerPath(String pathValue) {
        return String.format("/answers" + pathValue, defaultAnswer().orElseThrow(IllegalArgumentException::new).getId());
    }
}
