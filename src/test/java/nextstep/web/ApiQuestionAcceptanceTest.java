package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static nextstep.domain.UserTest.newUser;
import static org.assertj.core.api.Assertions.assertThat;


public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);


    @Test
    public void 로그인_사용자_글작성() throws Exception {
        Question createQuestion = new Question("제목원본", "내용원본");
        String location = createLoginUserResource("/api/questions", createQuestion, defaultUser());


        Question resource1 = getResource(location, Question.class, defaultUser());
        softly.assertThat(resource1).isNotNull();
    }

    @Test
    public void 로그인_안한_사용자_글작성_UNAUTHORIZED() {
        Question createQuestion = new Question("제목원본", "내용원본");

        ResponseEntity<Question> objectResponseEntity = basicAuthTemplate(User.GUEST_USER).postForEntity("/api/questions", createQuestion, Question.class);

        softly.assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    public void 사용자_글보기_가능() {
        Question createQuestion = new Question("제목원본", "내용원본");
        String location = createLoginUserResource("/api/questions", createQuestion, defaultUser());
        Question resource1 = getResource(location, Question.class, User.GUEST_USER);
        softly.assertThat(resource1).isNotNull();
    }

    @Test
    public void 자신의_글_업데이트_가능() {
        Question createQuestion = new Question("제목원본", "내용원본");
        Question updateQuestion = new Question("제목수정", "내용수정");
        String location = createLoginUserResource("/api/questions", createQuestion, defaultUser());

        ResponseEntity<Question> exchange = basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);
        softly.assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    public void 다른_유저의_글_업데이트_불가능() {
        Question createQuestion = new Question("제목원본", "내용원본");
        Question updateQuestion = new Question("제목수정", "내용수정");

        String location = createLoginUserResource("/api/questions", createQuestion, defaultUser());

        ResponseEntity<Question> exchange = basicAuthTemplate(User.GUEST_USER).exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    public void 본인글_삭제_가능() {
        Question createQuestion = new Question("제목원본", "내용원본");
        String location = createLoginUserResource("/api/questions", createQuestion, defaultUser());

        ResponseEntity<Question> exchange = basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.DELETE, null, Question.class);

        softly.assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 다른_유저의_글_삭제_불가능() {
        Question createQuestion = new Question("제목원본", "내용원본");
        String location = createLoginUserResource("/api/questions", createQuestion, defaultUser());

        ResponseEntity<Question> exchange = basicAuthTemplate(User.GUEST_USER).exchange(location, HttpMethod.DELETE, createHttpEntity(null), Question.class);
        softly.assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


}