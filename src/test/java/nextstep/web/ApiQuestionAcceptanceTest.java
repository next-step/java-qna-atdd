package nextstep.web;

import nextstep.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;


import static nextstep.domain.QuestionTest.newQuestion;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionController.class);
    public static final String API_QUESTIONS = "/api/questions";

    @Test
    public void create() throws Exception {
        Question newQuestion = newQuestion("Question 제목", "Question 내용");
        String location = createResource(basicAuthTemplate(), API_QUESTIONS, newQuestion);

        Question dbQuestion = getResource(template(), location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();
    }

    @Test
    public void create_by_guest() {
        Question newQuestion = newQuestion("Question 제목", "Question 내용");
        ResponseEntity<String> responseEntity = template().postForEntity(API_QUESTIONS, newQuestion, String.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<Question> updateQuesion(TestRestTemplate template, String location, Question question) {
        return updateResource(template, location, question, Question.class);
    }

    @Test
    public void update() {
        String location = createResource(basicAuthTemplate(), API_QUESTIONS, newQuestion("Question 제목", "Question 내용"));
        Question updatedQuestion = newQuestion("Question 제목 수정", "Question 내용 수정");
        ResponseEntity<Question> responseEntity = updateQuesion(basicAuthTemplate(), location, updatedQuestion);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updatedQuestion.equalsTitleAndContents(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_not_owner() {
        String location = createResource(basicAuthTemplate(), API_QUESTIONS, newQuestion("Question 제목", "Question 내용"));
        Question updatedQuestion = newQuestion("Question 제목 수정", "Question 내용 수정");
        ResponseEntity<Question> responseEntity = updateQuesion(basicAuthTemplate(findByUserId("sanjigi")), location, updatedQuestion);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_by_guest() {
        String location = createResource(basicAuthTemplate(), API_QUESTIONS, newQuestion("Question 제목", "Question 내용"));
        Question updatedQuestion = newQuestion("Question 제목 수정", "Question 내용 수정");
        ResponseEntity<Question> responseEntity = updateQuesion(template(), location, updatedQuestion);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() {
        String location = createResource(basicAuthTemplate(), API_QUESTIONS, newQuestion("Question 제목", "Question 내용"));
        ResponseEntity<Void> responseEntity = deleteResource(basicAuthTemplate(), location, Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_not_owner() {
        String location = createResource(basicAuthTemplate(), API_QUESTIONS, newQuestion("Question 제목", "Question 내용"));
        ResponseEntity<Void> responseEntity = deleteResource(basicAuthTemplate(findByUserId("sanjigi")), location, Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
