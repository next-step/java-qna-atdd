package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.QuestionTest;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import support.test.AcceptanceTest;

import java.text.MessageFormat;

import static nextstep.domain.AnswerTest.newAnswer;
import static support.test.WebAcceptanceTest.getResponseLocationPath;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    @Test
    @Transactional
    public void create() {
        User loginUser = defaultUser();
        ResponseEntity<Void> createResponseEntity = createAnswer(loginUser);
        Answer dbAnswer = selectAnswer(getResponseLocationPath(createResponseEntity));

        softly.assertThat(dbAnswer).isNotNull();
    }

    private ResponseEntity<Void> createAnswer(User loginUser) {
        Question question = defaultQuestion();
        Answer newAnswer = newAnswer(loginUser);

        String url = MessageFormat.format("/api/questions/{0}/answers", question.getId());

        ResponseEntity<Void> response = basicAuthTemplate(loginUser)
                                        .postForEntity(url, newAnswer, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response;
    }

    private Answer selectAnswer(String location) {
        return template().getForObject(location, Answer.class);
    }

    @Test
    @Transactional
    public void create_no_login() {
        Question newQuestion = QuestionTest.newTestQuestion();

        ResponseEntity<Void> response = template()
                .postForEntity("/api/questions", newQuestion, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Transactional
    public void update() {
        User loginUser = defaultUser();
        ResponseEntity<Void> createResponseEntity = createAnswer(loginUser);
        String location = getResponseLocationPath(createResponseEntity);

        Answer original = selectAnswer(location);
        Answer updateAnswer = new Answer(original.getId(), "변경된답변");

        ResponseEntity<Answer> responseEntity = basicAuthTemplate(loginUser)
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateAnswer), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateAnswer.equalsContents(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_no_login() {
        User loginUser = defaultUser();
        ResponseEntity<Void> createResponseEntity = createAnswer(loginUser);
        String location = getResponseLocationPath(createResponseEntity);


        Answer original = selectAnswer(location);
        Answer updateAnswer = new Answer(original.getId(), "변경된답변");

        ResponseEntity<String> responseEntity = template()
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateAnswer), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() {
        User loginUser = defaultUser();
        ResponseEntity<Void> createResponseEntity = createAnswer(loginUser);

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .exchange(getResponseLocationPath(createResponseEntity), HttpMethod.DELETE, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_no_login() {
        User loginUser = defaultUser();
        ResponseEntity<Void> createResponseEntity = createAnswer(loginUser);

        ResponseEntity<String> response = template()
                .exchange(getResponseLocationPath(createResponseEntity), HttpMethod.DELETE, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
