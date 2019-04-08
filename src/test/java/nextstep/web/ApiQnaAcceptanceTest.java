package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.dto.AnswerDTO;
import nextstep.dto.QuestionDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;

public class ApiQnaAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);

    private Question newQuestion;
    private String createLocation;

    @Autowired
    private QuestionRepository questionRepository;

    @Before
    public void setUp() {
        Question newQuestion = new Question("question title", "question contents");
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        createLocation = response.getHeaders().getLocation().getPath();

        this.newQuestion = newQuestion;
    }

    @After
    public void tearDown() {
        /*basicAuthTemplate().exchange(
                createLocation, HttpMethod.DELETE, createHttpEntity(newQuestion), Void.class);*/
        questionRepository.deleteAll();
    }

    @Test
    public void createQuestion() {
        Question newQuestion = new Question("question title", "question contents");
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        QuestionDTO question = getResource(location, QuestionDTO.class, defaultUser());
        softly.assertThat(question.getTitle()).isEqualTo("question title");
    }

    @Test
    public void updateQuestion() {
        Question updateQuestion = new Question("update title", "update title");

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange(createLocation, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateQuestion.getTitle().equals(responseEntity.getBody().getTitle())).isTrue();
    }

    @Test
    public void updateQuestion_not_owner() {
        Question updateQuestion = new Question("update title", "update title");
        User user = findByUserId("sanjigi");

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(user).exchange(createLocation, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void deleteQuestion() {
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange(
                createLocation, HttpMethod.DELETE, createHttpEntity(newQuestion), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void deleteQuestion_not_owner() {
        User user = findByUserId("sanjigi");
        ResponseEntity<Void> responseEntity = basicAuthTemplate(user).exchange(
                createLocation, HttpMethod.DELETE, createHttpEntity(newQuestion), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void deleteQuestion_not_found() {
        String location = "/api/questions/10";
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange(
                location, HttpMethod.DELETE, createHttpEntity(newQuestion), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void show() {
        ResponseEntity<QuestionDTO> responseEntity = template().getForEntity(createLocation, QuestionDTO.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().getTitle()).isEqualTo("question title");
    }

    @Test
    public void addAnswer() {
        Answer newAnswer = new Answer(defaultUser(), "answer test");
        String url = createLocation + "/answer/add";
        ResponseEntity<Answer> responseEntity = basicAuthTemplate().postForEntity(url, newAnswer, Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<QuestionDTO> questionDTO = template().getForEntity(createLocation, QuestionDTO.class);
        softly.assertThat(questionDTO.getBody().getAnswers().get(0).getContents()).isEqualTo("answer test");
    }

    @Test
    public void addAnswer_no_login() {
        Answer newAnswer = new Answer(defaultUser(), "answer test");
        String url = createLocation + "/answer/add";
        ResponseEntity<Answer> responseEntity = template().postForEntity(url, newAnswer, Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void deleteAnswer() {
        Answer newAnswer = new Answer(defaultUser(), "answer test");
        String url = createLocation + "/answer/add";
        ResponseEntity<Answer> responseEntity = basicAuthTemplate().postForEntity(url, newAnswer, Answer.class);

        Answer answer = responseEntity.getBody();
        AnswerDTO answerDTO = new AnswerDTO(answer);
        url = createLocation + "/answer/delete/";
        ResponseEntity<Void> deleteResponseEntity = basicAuthTemplate().exchange(url, HttpMethod.DELETE, createHttpEntity(answerDTO), Void.class);

        softly.assertThat(deleteResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void deleteAnswer_no_login() {
        Answer newAnswer = new Answer(defaultUser(), "answer test");
        String url = createLocation + "/answer/add";
        ResponseEntity<Answer> responseEntity = basicAuthTemplate().postForEntity(url, newAnswer, Answer.class);

        Answer answer = responseEntity.getBody();
        AnswerDTO answerDTO = new AnswerDTO(answer);
        url = createLocation + "/answer/delete/";
        ResponseEntity<Void> deleteResponseEntity = template().exchange(url, HttpMethod.DELETE, createHttpEntity(answerDTO), Void.class);

        softly.assertThat(deleteResponseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void deleteAnswer_not_found() {
        Answer answer = new Answer(10L, defaultUser(), newQuestion, "answer test");
        String url = createLocation + "/answer/delete/";
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange(url, HttpMethod.DELETE, createHttpEntity(answer), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }

}
