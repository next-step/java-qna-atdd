package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.UserTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private String location;

    @Before
    public void setUp() throws Exception {
        location = createResource("/api/questions/1/answers", "답변입니다", defaultUser());
    }

    @Test
    public void 답변_생성_로그인_유저() {
        String answerContents = "답변입니다";
        String location = createResource("/api/questions/1/answers", answerContents, defaultUser());

        Answer created = getResource(location, Answer.class, defaultUser());

        softly.assertThat(created.getContents()).isEqualTo(answerContents);
    }

    @Test
    public void 미로그인_유저는_답변할_수_없다() {
        String answerContents = "답변입니다";
        ResponseEntity<String> response = template().postForEntity("/api/questions/1/answers", answerContents, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 내_답변_수정() {
        String answerContents = "업데이트합니다";

        Answer answer = getResource(location, Answer.class, defaultUser());

        ResponseEntity<Answer> response = basicAuthTemplate()
            .exchange("/api" + answer.generateUrl(), HttpMethod.PUT, createHttpEntity(answerContents), Answer.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().getContents()).isEqualTo(answerContents);
    }

    @Test
    public void 내_답변이_아니면_수정할_수_없다() {
        String answerContents = "업데이트합니다";

        Answer answer = getResource(location, Answer.class, defaultUser());

        ResponseEntity<Answer> response = basicAuthTemplate(UserTest.SANJIGI)
            .exchange("/api" + answer.generateUrl(), HttpMethod.PUT, createHttpEntity(answerContents), Answer.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 내_답변_삭제() {
        HttpEntity<Object> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<Void> response = basicAuthTemplate()
            .exchange(location, HttpMethod.DELETE, entity, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Answer> response2 = basicAuthTemplate().getForEntity(location, Answer.class);
        softly.assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 내_답변이_아니면_삭제할_수_없다() {
        HttpEntity<Object> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<Void> response = basicAuthTemplate(UserTest.SANJIGI)
            .exchange(location, HttpMethod.DELETE, entity, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
