package nextstep.web;

import nextstep.domain.Answer;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.net.URI;
import java.net.URISyntaxException;

import static nextstep.domain.UserTest.JAVAJIGI;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private final String API_ANSWERS = "/api/answers";

    @Test
    public void create() throws Exception {
        Answer postAnswer = new Answer(JAVAJIGI, "TDD 합시다");
        ResponseEntity<Answer> answerResponse = getExchange(new URI(API_ANSWERS + "/1/answer"), HttpMethod.POST, createHttp(postAnswer), Answer.class);

        softly.assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

   @Test
    public void delete() throws URISyntaxException {

       ResponseEntity<Answer> answerResponse = getExchange(new URI(API_ANSWERS + "/1/answer/1"), HttpMethod.DELETE, emptyHttp(), Answer.class);
       Answer resultResponse = answerResponse.getBody();

       softly.assertThat(resultResponse.isDeleted()).isFalse();
    }
}
