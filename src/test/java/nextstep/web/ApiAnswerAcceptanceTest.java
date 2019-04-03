package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);


    @Test
    public void 로그인_사용자_댓글_작성_가능() {

        Answer answer = new Answer(defaultUser(), "댓글원본");
        String loginUserResource = createLoginUserResource("/api/questions/1/answers", answer, defaultUser());
        Answer resource = getResource(loginUserResource, Answer.class, defaultUser());

        softly.assertThat(resource).isNotNull();
        log.debug("resource.getContents : ",resource.getContents());
    }

    @Test
    public void 로그인_안한_사용자_댓글_작성_불가능() {
        Answer answer = new Answer(defaultUser(), "댓글원본");
        ResponseEntity<String> responseEntity = basicAuthTemplate(User.GUEST_USER).postForEntity("/api/questions/1/answers", answer, String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 자신의_댓글_삭제_가능() {
        ResponseEntity<Answer> exchange =
            basicAuthTemplate(defaultUser()).exchange("/api/questions/1/answers/3", HttpMethod.DELETE, createHttpEntity(null), Answer.class);
        softly.assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 다른_유저의_댓글_삭제_불가능() {
        ResponseEntity<Answer> exchange =
            basicAuthTemplate(defaultUser()).exchange("/api/questions/1/answers/1", HttpMethod.DELETE, createHttpEntity(null), Answer.class);
        softly.assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    public void 다른_유저sd의_댓글_삭제_불가능() {
        ResponseEntity<Answer> exchange =
            basicAuthTemplate(new User(3,"id","password","name","email@.com")).exchange("/api/questions/1/answers/1", HttpMethod.DELETE, createHttpEntity(null), Answer.class);
        softly.assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
