package nextstep.web.api;

import nextstep.domain.Question;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.ApiAcceptanceTest;

public class ApiQuestionAcceptanceTest extends ApiAcceptanceTest {

    @Test
    public void get() throws Exception {
        Question question = insertTestQuestion("title", "contents");
        String url = "/api/questions/" + question.getId();

        Question resultQuestion = getResource(url, Question.class);

        softly.assertThat(resultQuestion.getTitle()).isEqualTo(question.getTitle());
        softly.assertThat(resultQuestion.getContents()).isEqualTo(question.getContents());
    }

    @Test
    public void create() throws Exception {
        Question question = new Question("title", "contents");
        String resourceLocation = createResource("/api/questions", defaultUser(), question);

        Question resultQuestion = template().getForObject(resourceLocation, Question.class);
        softly.assertThat(resultQuestion).isNotNull();
    }

    @Test
    public void update() throws Exception {
        Question prevQuestion = insertTestQuestion("title", "contents");
        String url = "/api/questions/" + prevQuestion.getId();

        Question question = new Question("newTitle", "newContents");

        ResponseEntity<Question> response = modifyResourceResponseEntity(url, defaultUser(), question, Question.class);
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

        ResponseEntity<Question> response = modifyResourceResponseEntity(url, anotherUser, question, Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    public void delete() throws Exception {
        Question question = insertTestQuestion("title", "contents");
        String url = "/api/questions/" + question.getId();

        ResponseEntity<Void> response = deleteResourceResponseEntity(url, defaultUser());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_invalidUser() throws Exception {
        Question question = insertTestQuestion("title", "contents");
        String url = "/api/questions/" + question.getId();

        ResponseEntity<Void> response = deleteResourceResponseEntity(url, anotherUser);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


}
