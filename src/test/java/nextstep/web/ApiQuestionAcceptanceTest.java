package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.domain.UserTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import java.util.Optional;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void create() throws Exception {
        User loginUser = defaultUser();
        Question question = new Question("title", "content");

        ResponseEntity<Void> response = basicAuthTemplate(loginUser).postForEntity("/api/questions", question, Void.class);
        // 왜 question의 title과 content의 내용을 한글로 세팅하면 테스트를 통과하지 못할까요?
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Optional<Question> dbQuestion = questionRepository.findById(question.getId());
        softly.assertThat(dbQuestion).isNotNull();
    }

    @Test
    public void show() throws Exception {
        Question question = template().getForObject("/api/questions/1", Question.class);
        softly.assertThat(question).isNotNull();
    }

    @Test
    public void update() throws Exception {
        User loginUser = defaultUser();
        Question question = questionRepository.findById(1L).get();
        Question otherQuestion = new Question("modify title", "modify contents");
        otherQuestion.writeBy(loginUser);
        Question updatedQuestion = question.update(otherQuestion);
        ResponseEntity<Question> responseEntity = basicAuthTemplate(loginUser).exchange("/api/questions/1", HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updatedQuestion.equalsTitleAndContents(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_다른사용자() throws Exception {
        User loginUser = defaultUser();
        Question newQuestion = new Question("title", "content");
        newQuestion.writeBy(loginUser);
        Question otherQuestion = new Question("modify title", "modify contents");
        otherQuestion.writeBy(loginUser);
        Question updatedQuestion = newQuestion.update(otherQuestion);

        ResponseEntity<Question> responseEntity = basicAuthTemplate(UserTest.SANJIGI).exchange("/api/questions/1", HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void update_no_login() throws Exception {
        User loginUser = defaultUser();
        Question newQuestion = new Question("title", "content");
        newQuestion.writeBy(loginUser);
        Question otherQuestion = new Question("modify title", "modify contents");
        otherQuestion.writeBy(loginUser);
        Question updatedQuestion = newQuestion.update(otherQuestion);

        ResponseEntity<Question> responseEntity = template().exchange("/api/questions/1", HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }
}
