package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.AnswerRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.stream.Collectors;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);

    @Autowired
    private AnswerRepository answerRepository;



    @Test
    public void 로그인_사용자_댓글_작성_가능() {

        Answer answer = new Answer(defaultUser(), "댓글원본");
        String loginUserResource = createLoginUserResource("/api/questions/1/answers", answer, defaultUser());
        Answer resource = getResource(loginUserResource, Answer.class, defaultUser());

        softly.assertThat(resource).isNotNull();
        softly.assertThat(answerRepository.findAll()
            .stream()
            .map(Answer::getContents)
            .filter(contents -> contents.contains("댓글원본"))
            .collect(Collectors.toList())
            .size()).isEqualTo(1);
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
        softly.assertThat(answerRepository.findById(3L).get().isDeleted()).isTrue();
    }

    @Test
    public void 다른_유저의_댓글_삭제_불가능() {
        ResponseEntity<Answer> exchange =
            basicAuthTemplate(defaultUser()).exchange("/api/questions/1/answers/1", HttpMethod.DELETE, createHttpEntity(null), Answer.class);
        softly.assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        softly.assertThat(answerRepository.findById(1L).get().isDeleted()).isFalse();
    }


    @Test
    public void 다른_유저의_댓글_삭제_불가능2() {
        ResponseEntity<Answer> exchange =
            basicAuthTemplate(new User(3,"id","password","name","email@.com")).exchange("/api/questions/1/answers/1", HttpMethod.DELETE, createHttpEntity(null), Answer.class);
        softly.assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
