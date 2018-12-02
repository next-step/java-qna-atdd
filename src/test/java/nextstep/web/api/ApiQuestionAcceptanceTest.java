package nextstep.web.api;

import nextstep.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.ApiAcceptanceTest;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;

public class ApiQuestionAcceptanceTest extends ApiAcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Test
    public void get() throws Exception {
        Question question = insertTestQuestion("title", "contents");
        String url = "/api/questions/" + question.getId();

        ResponseEntity<Question> response = template().getForEntity(url, Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        softly.assertThat(question.getTitle()).isEqualTo(question.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(question.getContents());
    }

    @Test
    public void create() throws Exception {
        Question question = new Question("title", "contents");
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", question, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String location = response.getHeaders().getLocation().getPath();
        Question resultQuestion = template().getForObject(location, Question.class);
        softly.assertThat(resultQuestion).isNotNull();
    }

    @Test
    public void update() throws Exception {
        Question prevQuestion = insertTestQuestion("title", "contents");
        String url = "/api/questions/" + prevQuestion.getId();

        Question question = new Question("newTitle", "newContents");

        ResponseEntity<Question> response = basicAuthTemplate().exchange(url, PUT, createHttpEntity(question), Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Question resultQuestion = template().getForObject(url, Question.class);
        softly.assertThat(resultQuestion.getTitle()).isEqualTo(question.getTitle());
        softly.assertThat(resultQuestion.getContents()).isEqualTo(question.getContents());
    }

    @Test
    public void update_invalidUser() throws Exception {
        Question prevQuestion = insertTestQuestion("title", "contents");
        String url = "/api/questions/" + prevQuestion.getId();

        Question question = new Question("newTitle", "newContents");

        ResponseEntity<Question> response = basicAuthTemplate(anotherUser).exchange(url, PUT, createHttpEntity(question), Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    public void delete() throws Exception {
        Question question = insertTestQuestion("title", "contents");
        String url = "/api/questions/" + question.getId();

        ResponseEntity<Void> response = basicAuthTemplate().exchange(url, DELETE, null, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_invalidUser() throws Exception {
        Question question = insertTestQuestion("title", "contents");
        String url = "/api/questions/" + question.getId();

        ResponseEntity<Void> response = basicAuthTemplate(anotherUser).exchange(url, DELETE, null, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


}
