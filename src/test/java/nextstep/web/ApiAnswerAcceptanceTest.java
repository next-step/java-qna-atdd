package nextstep.web;

import nextstep.domain.entity.Answer;
import nextstep.domain.entity.Question;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private static final String QUESTION_PATH = "/api/questions";
    private static final String DEFAULT_ANSWER_PATH = "/api/questions/1/answers";
    private Question testQuestion;

    @Before
    public void setUp() throws Exception {
        testQuestion = new Question("newTitle", "newContents");
    }

    @Test
    public void create() {
        String location = createResourceWithLogin(QUESTION_PATH, testQuestion, defaultUser());
        Question savedQuestion = getResourceWithLogin(location, Question.class, defaultUser());
        softly.assertThat(savedQuestion).isNotNull();

        String contents = "댓글입니다";
        String answerLocation = location + "/answers";
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity(answerLocation, contents, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void create_no_login() {
        String location = testQuestion.generateApiUrl() + "/answers";
        String contents = "댓글입니다";
        ResponseEntity<Void> response = template().postForEntity(location, contents, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void show() {
        String location = createResourceWithLogin(DEFAULT_ANSWER_PATH, "답변내용입니다.", defaultUser());
        Answer answer = getResourceWithLogin(location, Answer.class, defaultUser());
        softly.assertThat(answer.getContents()).isEqualTo("답변내용입니다.");
    }

    @Test
    public void delete() {
        String location = createResourceWithLogin(DEFAULT_ANSWER_PATH, "답변내용입니다.", defaultUser());
        //TODO : getResource를 통해 가져온 Answer에서 URI 만들기
        ResponseEntity<Answer> deletedAnswer = basicAuthTemplate()
                .exchange(location, HttpMethod.DELETE, createHttpEntity(null), Answer.class);

        softly.assertThat(deletedAnswer.getBody().isDeleted()).isTrue();
    }

    @Test
    public void update() {
        String location = createResourceWithLogin(DEFAULT_ANSWER_PATH, "원래답변은 이래", defaultUser());
        Answer answer = getResourceWithLogin(location, Answer.class, defaultUser());
        ResponseEntity<Answer> response = basicAuthTemplate()
                .exchange(location, HttpMethod.PUT, createHttpEntity("수정한 답변"), Answer.class);
        softly.assertThat(response.getBody().getContents()).isEqualTo("수정한 답변");
    }

    @Test //TODO : 형구님 이 CASE에서 answer 객체 내부에 있는 question객체가 null 입니다.
    public void question객체가_null() {
        String location = createResourceWithLogin(DEFAULT_ANSWER_PATH, "원래답변은 이래", defaultUser());
        Answer answer = getResourceWithLogin(location, Answer.class, defaultUser());
        ResponseEntity<Answer> response = basicAuthTemplate()
                .exchange(answer.generateApiUrl(), HttpMethod.PUT, createHttpEntity("수정한 답변"), Answer.class);
        softly.assertThat(response.getBody().getContents()).isEqualTo("수정한 답변");
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);

    }
}