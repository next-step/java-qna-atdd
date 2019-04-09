package nextstep.web;

import nextstep.domain.entity.Question;
import nextstep.domain.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private static final String CREATE_PATH = "/api/questions";
    private Question testQuestion;

    @Before
    public void setUp() throws Exception {
        testQuestion = new Question("newTitle", "newContents");
    }

    @Test
    public void create() {
        String location = createResourceWithLogin(CREATE_PATH, testQuestion, defaultUser());
        Question savedQuestion = getResourceWithLogin(location, Question.class, defaultUser());
        softly.assertThat(savedQuestion).isNotNull();
    }

    //GET
    @Test
    public void show() {
        String location = createResourceWithLogin(CREATE_PATH, testQuestion, defaultUser());
        Question response = template().getForObject(location, Question.class);
        softly.assertThat(response).isNotNull();
    }

    //PUT
    @Test
    public void update_success() {
        String location = createResourceWithLogin(CREATE_PATH, testQuestion, defaultUser());
        Question originalQuestion = getResourceWithLogin(location, Question.class, defaultUser());

        Question newQuestion = new Question("updateTitle", "updateContents");
        ResponseEntity<Question> response = basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(newQuestion), Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().getId()).isEqualTo(originalQuestion.getId());
    }

    @Test
    public void update_no_login() {
        String location = createResourceWithLogin(CREATE_PATH, testQuestion, defaultUser());

        Question updateQuestion = new Question("수정할 제목", "수정할 내용");
        ResponseEntity<String> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), String.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    public void updateQuestion_다른사람() {
        String location = createResourceWithLogin(CREATE_PATH, testQuestion, defaultUser());
        Question originalQuestion = getResourceWithLogin(location, Question.class, defaultUser());

        User newUser = new User("admin", "password", "spring", "spring@gmail.com");
        User owner = getResourceWithLogin(createResource("/api/users", newUser), User.class, newUser);

        Question newQuestion = new Question("update", "성공할거야");
        ResponseEntity<Question> response = basicAuthTemplate(owner)
                .exchange(originalQuestion.generateApiUrl(), HttpMethod.PUT, createHttpEntity(newQuestion), Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_없는게시글() {
        String location = createResourceWithLogin(CREATE_PATH, testQuestion, defaultUser());
        ResponseEntity<String> response = basicAuthTemplate()
                .exchange("/api/questions/-1", HttpMethod.PUT, createHttpEntity(testQuestion), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    //DELETE
    @Test
    public void delete_success() {
        String location = createResourceWithLogin(CREATE_PATH, testQuestion, defaultUser());
        ResponseEntity<String> response = basicAuthTemplate()
                .exchange(location, HttpMethod.DELETE, createHttpEntity(testQuestion), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_다른사람() {
        String location = createResourceWithLogin(CREATE_PATH, testQuestion, defaultUser());
        User newUser = new User("admin", "password", "spring", "spring@gmail.com");
        ResponseEntity<String> response = basicAuthTemplate(newUser)
                .exchange(location, HttpMethod.DELETE, createHttpEntity(testQuestion), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_없는게시글() {
        String location = createResourceWithLogin(CREATE_PATH, testQuestion, defaultUser());
        ResponseEntity<String> response = basicAuthTemplate()
                .exchange("/api/questions/-1", HttpMethod.DELETE, createHttpEntity(testQuestion), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);

    }
}
