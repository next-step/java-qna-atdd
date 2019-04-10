package support.test;

import nextstep.domain.*;
import nextstep.service.QnaService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest extends BaseTest {

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

    protected Question selfQuestion() {
        return questionRepository.findById(DEFAULT_QUESTION_ID).get();
    }

    protected Question anotherQuestion() {
        return questionRepository.findById(ANOTHER_QUESTION_ID).get();
    }

    protected Answer selfAnswer() {
        return answerRepository.findById(DEFAULT_ANSWER_ID).get();
    }

    protected Answer anotherAnswer() {
        return answerRepository.findById(ANOTHER_ANSWER_ID).get();
    }
}
