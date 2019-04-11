package support.test;

import nextstep.domain.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static nextstep.domain.AnswerTest.ANOTHER_ANSWER_ID;
import static nextstep.domain.AnswerTest.SELF_ANSWER_ID;
import static nextstep.domain.QuestionTest.ANOTHER_QUESTION_ID;
import static nextstep.domain.QuestionTest.SELF_QUESTION_ID;
import static nextstep.domain.UserTest.ANOTHER_USER;
import static nextstep.domain.UserTest.SELF_USER;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest extends BaseTest {

    @Autowired
    protected TestRestTemplate template;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected QuestionRepository questionRepository;

    @Autowired
    protected AnswerRepository answerRepository;

    @Autowired
    protected DeleteHistoryRepository deleteHistoryRepository;

    public TestRestTemplate template() {
        return template;
    }

    public TestRestTemplate basicAuthTemplate() {
        return basicAuthTemplate(selfUser());
    }

    public TestRestTemplate basicAuthTemplate(User loginUser) {
        return template.withBasicAuth(loginUser.getUserId(), loginUser.getPassword());
    }

    protected User selfUser() {
        return findByUserId(SELF_USER.getUserId());
    }

    protected User anotherUser() {
        return findByUserId(ANOTHER_USER.getUserId());
    }

    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).get();
    }

    protected Question selfQuestion() {
        return questionRepository.findById(SELF_QUESTION_ID).get();
    }

    protected Question anotherQuestion() {
        return questionRepository.findById(ANOTHER_QUESTION_ID).get();
    }

    protected Answer selfAnswer() {
        return answerRepository.findById(SELF_ANSWER_ID).get();
    }

    protected Answer anotherAnswer() {
        return answerRepository.findById(ANOTHER_ANSWER_ID).get();
    }
}
