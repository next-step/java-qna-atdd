package nextstep.web.api;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.service.QnaService;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.ApiAcceptanceTest;

public class ApiAnswerAcceptanceTest extends ApiAcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    private Question parentQuestion;

    @Before
    public void setup() {
        parentQuestion = insertTestQuestion("title", "contents");
    }

    @Test
    public void create() throws Exception {
        String answerUrl = "/api/" + parentQuestion.generateUrl() + "/answers";
        String contents = "answerContents";

        String resultPath = createResource(answerUrl, defaultUser(), contents);
        Answer resultAnswer = getResource(resultPath, Answer.class);
        softly.assertThat(resultAnswer).isNotNull();
    }

    @Test
    public void get() throws Exception {
        Answer answer = addTestAnswer(parentQuestion, "answerContents");

        Answer resultAnswer = getResource(apiAnswerUrl(answer), Answer.class);
        softly.assertThat(resultAnswer.getContents()).isEqualTo(answer.getContents());
    }

    @Test
    public void update() throws Exception {
        Answer answer = addTestAnswer(parentQuestion, "answerContents");

        String newContents = "newAnswerContents";
        ResponseEntity<Answer> response = modifyResourceResponseEntity(apiAnswerUrl(answer), defaultUser(), newContents, Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Answer resultAnswer = response.getBody();
        softly.assertThat(resultAnswer.getContents()).isEqualTo(newContents);
    }

    private String apiAnswerUrl(Answer answer) {
        return "/api/" + answer.generateUrl();
    }

    @Test
    public void update_invalidUser() throws Exception {
        Answer answer = addTestAnswer(parentQuestion, "answerContents");

        ResponseEntity<Answer> response = modifyResourceResponseEntity(apiAnswerUrl(answer), anotherUser, "test", Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() throws Exception {
        Answer answer = addTestAnswer(parentQuestion, "answerContents");

        ResponseEntity<Void> response = deleteResourceResponseEntity(apiAnswerUrl(answer), defaultUser());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_invalidUser() throws Exception {
        Answer answer = addTestAnswer(parentQuestion, "answerContents");

        ResponseEntity<Void> response = deleteResourceResponseEntity(apiAnswerUrl(answer), anotherUser);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Autowired
    private QnaService qnaService;

    public Answer addTestAnswer(Question question, String contents) {
        return qnaService.addAnswer(defaultUser(), question.getId(), contents);
    }


}
