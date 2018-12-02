package nextstep.web.api;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.service.QnaService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.ApiAcceptanceTest;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;

public class ApiAnswerAcceptanceTest extends ApiAcceptanceTest {
    private Question parentQuestion;

    @Before
    public void setup() {
        parentQuestion = insertTestQuestion("title", "contents");
    }

    @Test
    public void create() throws Exception {
        String answerUrl = "/api/" + parentQuestion.generateUrl() + "/answers";
        String contents = "answerContents";

        ResponseEntity<Void> response = basicAuthTemplate().postForEntity(answerUrl, contents, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String location = response.getHeaders().getLocation().getPath();
        Answer resultAnswer = template().getForObject(location, Answer.class);
        softly.assertThat(resultAnswer).isNotNull();
    }

    @Test
    public void get() throws Exception {
        Answer answer = addTestAnswer(parentQuestion, "answerContents");

        ResponseEntity<Answer> response = basicAuthTemplate().getForEntity(apiAnswerUrl(answer), Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Answer resultAnswer = response.getBody();
        softly.assertThat(resultAnswer.getQuestion()).isEqualTo(answer.getQuestion());
        softly.assertThat(resultAnswer.getContents()).isEqualTo(answer.getContents());
    }

    @Test
    public void update() throws Exception {
        Answer answer = addTestAnswer(parentQuestion, "answerContents");

        String newContents = "newAnswerContents";
        ResponseEntity<Answer> response = basicAuthTemplate().exchange(apiAnswerUrl(answer), PUT, createHttpEntity(newContents), Answer.class);
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

        ResponseEntity<Answer> response = basicAuthTemplate(anotherUser).exchange(apiAnswerUrl(answer), PUT, createHttpEntity("test"), Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() throws Exception {
        Answer answer = addTestAnswer(parentQuestion, "answerContents");

        ResponseEntity<Void> response = basicAuthTemplate().exchange(apiAnswerUrl(answer), DELETE, null, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_invalidUser() throws Exception {
        Answer answer = addTestAnswer(parentQuestion, "answerContents");

        ResponseEntity<Void> response = basicAuthTemplate(anotherUser).exchange(apiAnswerUrl(answer), DELETE, null, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Autowired
    private QnaService qnaService;

    public Answer addTestAnswer(Question question, String contents) {
        return qnaService.addAnswer(defaultUser(), question.getId(), contents);
    }


}
