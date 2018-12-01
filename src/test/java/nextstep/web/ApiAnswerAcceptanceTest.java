package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.AnswerTest;
import nextstep.domain.QuestionTest;
import nextstep.domain.UserTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private String questionUrl;
    private String createdUrl;

    @Before
    public void setUp() throws Exception {
        questionUrl = createResource("/api/questions", QuestionTest.newQuestion(), defaultUser());
        createdUrl = createResource(questionUrl + "/answers", AnswerTest.newAnswer().getContents(), defaultUser());
    }

    @Test
    public void 답변_생성_로그인_유저() {
        Answer created = getResource(createdUrl, Answer.class, defaultUser());

        softly.assertThat(created.getContents()).isEqualTo(AnswerTest.newAnswer().getContents());
    }

    @Test
    public void 미로그인_유저는_답변할_수_없다() {
        ResponseEntity<String> response = template()
            .postForEntity(questionUrl + "/answers", AnswerTest.newAnswer().getContents(), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 내_답변_수정() {
        String answerContents = "업데이트합니다";

        Answer answer = getResource(createdUrl, Answer.class, defaultUser());

        ResponseEntity<Answer> response = basicAuthTemplate()
            .exchange("/api" + answer.generateUrl(), HttpMethod.PUT, createHttpEntity(answerContents), Answer.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().getContents()).isEqualTo(answerContents);
    }

    @Test
    public void 내_답변이_아니면_수정할_수_없다() {
        String answerContents = "업데이트합니다";

        Answer answer = getResource(createdUrl, Answer.class, defaultUser());

        ResponseEntity<Answer> response = basicAuthTemplate(UserTest.SANJIGI)
            .exchange("/api" + answer.generateUrl(), HttpMethod.PUT, createHttpEntity(answerContents), Answer.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 내_답변_삭제() {
        HttpEntity<Object> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<Void> response = basicAuthTemplate()
            .exchange(createdUrl, HttpMethod.DELETE, entity, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Answer> response2 = basicAuthTemplate().getForEntity(createdUrl, Answer.class);
        softly.assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 내_답변이_아니면_삭제할_수_없다() {
        HttpEntity<Object> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<Void> response = basicAuthTemplate(UserTest.SANJIGI)
            .exchange(createdUrl, HttpMethod.DELETE, entity, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
