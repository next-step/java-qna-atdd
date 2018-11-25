package nextstep.web;

import nextstep.QuestionNotFoundException;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import javax.servlet.http.HttpSession;
import java.util.Optional;


public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void 질문_생성() {
        User loginUser = defaultUser();
        Question question = new Question("title", "test contents");

        ResponseEntity<Void> response = basicAuthTemplate(loginUser)
                .postForEntity("/api/questions", question, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Optional<Question> dbQuestion = questionRepository.findById(question.getId());
        softly.assertThat(dbQuestion).isNotNull();
    }

    @Test
    public void 모든_질문_조회() {
        ResponseEntity<Void> response = template()
                .getForEntity("/api/questions", Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 질문_id별_조회() {
        Question question = template().getForObject("/api/questions/1", Question.class);
        softly.assertThat(question).isNotNull();
    }

    @Test
    public void 질문_update() {
        User loginUser = defaultUser();
        Question updateQuestion = new Question("test update", "test update content");

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(loginUser).exchange("/api/questions/1", HttpMethod.POST, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateQuestion.equalsTitleAndContents(responseEntity.getBody())).isTrue();
    }

    @Test
    public void 질문_update_다른사용자() {
        User loginUser = findByUserId("sanjigi");
        Question updateQuestion = new Question("test update", "test update content");

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(loginUser).exchange("/api/questions/1", HttpMethod.POST, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 질문_삭제() {
        User loginUser = defaultUser();

        basicAuthTemplate(loginUser).delete("/api/questions/1");
        softly.assertThat(questionRepository.findById(1L).orElseThrow(QuestionNotFoundException::new).isDeleted()).isTrue();
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
