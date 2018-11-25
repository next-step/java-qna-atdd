package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.User;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static nextstep.domain.UserTest.newUser;


public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    @Test
    public void 질문에_대한_답변_생성() {
        User loginUser = newUser("testuser4");
        createResource("/api/users", loginUser);

        String answerContent = "this is answer";

        ResponseEntity<Void> response = getResponseByExchange("/api/answers/2/questions", createHttpEntity(answerContent), Void.class, loginUser, HttpMethod.POST);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Answer answer = getResource("/api/answers//3", Answer.class, loginUser);
        softly.assertThat(answer).isNotNull();
    }

    @Test
    public void 질문에_대한_답변_삭제() {
        User loginUser = defaultUser();

        basicAuthTemplate(loginUser).delete("/api/answers/1");

        Answer answer = getResource("/api/answers/1", Answer.class, loginUser);
        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test
    public void 질문에_대한_답변_수정() {
        User loginUser = defaultUser();
        String update = "test_update";
        ResponseEntity<Answer> responseEntity = getResponseByExchange("/api/answers/1", createHttpEntity(update), Answer.class, loginUser, HttpMethod.POST);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().getContents().equals(update)).isTrue();
    }
}