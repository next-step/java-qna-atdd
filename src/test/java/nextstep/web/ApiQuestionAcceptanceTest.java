package nextstep.web;

import nextstep.domain.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.net.URI;
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
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Optional<Question> dbQuestion = questionRepository.findById(question.getId());
        softly.assertThat(dbQuestion).isNotNull();
    }

    @Test
    public void show() throws Exception {
        Question question = template().getForObject("/api/questions/1", Question.class);
        softly.assertThat(question).isNotNull();
    }

    public Question findQuestionIdNotDeleted(Long id) {
        return questionRepository.findByIdAndDeleted(id, false).orElseThrow(EntityNotFoundException::new);
    }

    @Test
    public void update() throws Exception {
        // 1. 질문 작성
        Question question = new Question("title", "content");
        ResponseEntity<Question> questionWriteResponse = basicAuthTemplate(defaultUser()).postForEntity("/api/questions", question, Question.class);
        String questionResource = questionWriteResponse.getHeaders().getLocation().getPath();
        Question origin = getResource(questionResource, Question.class, defaultUser());

        // 2. 질문 수정
        Question otherQuestion = new Question("modify title", "modify contents");
        ResponseEntity<Question> responseEntity = basicAuthTemplate(defaultUser()).exchange("/api/questions/" + origin.getId(), HttpMethod.PUT, createHttpEntity(otherQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(otherQuestion.equalsTitleAndContents(responseEntity.getBody())).isTrue();
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

    @Test
    public void delete_질문없는경우() throws Exception {
        String noQuestionResource = "/api/questions";

        // 1. 질문 삭제
        ResponseEntity<Void> deleteQuestionResponse = basicAuthTemplate(defaultUser()).exchange(noQuestionResource, HttpMethod.DELETE, createHttpEntity(Void.class), Void.class);

        softly.assertThat(deleteQuestionResponse.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    public void delete_답변없는경우() throws Exception {
        // 1. 질문 작성
        Question question = new Question("title", "content");
        ResponseEntity<Question> questionWriteResponse = basicAuthTemplate(defaultUser()).postForEntity("/api/questions", question, Question.class);
        String questionResource = questionWriteResponse.getHeaders().getLocation().getPath();
        Question origin = getResource(questionResource, Question.class, defaultUser());

        // 3. 질문 삭제
        ResponseEntity<Void> deleteQuestionResponse = basicAuthTemplate(defaultUser()).exchange(questionResource, HttpMethod.DELETE, createHttpEntity(origin), Void.class);
        final ResponseEntity<Question> isQuestionDeleted = basicAuthTemplate(defaultUser()).getForEntity(questionResource, Question.class);

        softly.assertThat(deleteQuestionResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        softly.assertThat(isQuestionDeleted.getStatusCode()).isSameAs(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete_질문자_로그인_다른경우() throws Exception {
        // 1. 질문 작성
        Question question = new Question("title", "content");
        ResponseEntity<Question> questionWriteResponse = basicAuthTemplate(defaultUser()).postForEntity("/api/questions", question, Question.class);
        String questionResource = questionWriteResponse.getHeaders().getLocation().getPath();
        Question origin = getResource(questionResource, Question.class, defaultUser());

        // 3. 질문 삭제
        ResponseEntity<Void> deleteQuestionResponse = basicAuthTemplate(anotherUser()).exchange(questionResource, HttpMethod.DELETE, createHttpEntity(origin), Void.class);

        softly.assertThat(deleteQuestionResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_질문자_답변자_같은경우() throws Exception {
        // 1. 질문 작성
        Question question = new Question("title", "content");
        ResponseEntity<Question> questionWriteResponse = basicAuthTemplate(defaultUser()).postForEntity("/api/questions", question, Question.class);
        String questionResource = questionWriteResponse.getHeaders().getLocation().getPath();
        Question origin = getResource(questionResource, Question.class, defaultUser());

        // 2. 답변 추가
        Answer answer = new Answer(defaultUser(), "original answer");
        post(answer, basicAuthTemplate(), origin.getId());

        // 3. 질문 삭제
        ResponseEntity<Void> deleteQuestionResponse = basicAuthTemplate(defaultUser()).exchange(questionResource, HttpMethod.DELETE, createHttpEntity(origin), Void.class);
        final ResponseEntity<Question> isQuestionDeleted = basicAuthTemplate(defaultUser()).getForEntity(questionResource, Question.class);

        softly.assertThat(deleteQuestionResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        softly.assertThat(isQuestionDeleted.getStatusCode()).isSameAs(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete_질문자_답변자_다른경우() throws Exception {
        // 1. 질문 작성
        Question question = new Question("title", "content");
        ResponseEntity<Question> questionWriteResponse = basicAuthTemplate(defaultUser()).postForEntity("/api/questions", question, Question.class);
        String questionResource = questionWriteResponse.getHeaders().getLocation().getPath();
        Question origin = getResource(questionResource, Question.class, defaultUser());

        // 2. 답변 추가
        Answer answer = new Answer(anotherUser(), "another answer");
        post(answer, basicAuthTemplate(anotherUser()), origin.getId());

        // 3. 질문 삭제
        ResponseEntity<Void> deleteQuestionResponse = basicAuthTemplate(defaultUser()).exchange(questionResource, HttpMethod.DELETE, createHttpEntity(origin), Void.class);

        softly.assertThat(deleteQuestionResponse.getStatusCode()).isSameAs(HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<Answer> post(Answer answer, TestRestTemplate testRestTemplate, Long id) {
        return testRestTemplate.postForEntity("/api/questions/{id}/answers", answer, Answer.class, id);
    }
}
