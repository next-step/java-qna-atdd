package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.AnswerRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static nextstep.domain.UserTest.JAVAJIGI;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private final String API_ANSWERS = "/api/answers";

    @Resource(name = "answerRepository")
    AnswerRepository answerRepository;

    @Test
    public void create() throws Exception {
        Answer postAnswer = new Answer(JAVAJIGI, "TDD 합시다");
        ResponseEntity<Answer> answerResponse = getExchange(new URI(API_ANSWERS + "/1/answer"), HttpMethod.POST, createHttp(postAnswer), Answer.class);

        softly.assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

   @Test
    public void delete() throws URISyntaxException {
       ResponseEntity<Answer> answerResponse = getExchange(new URI(API_ANSWERS + "/1/answer"), HttpMethod.POST, createHttp(new Answer(JAVAJIGI, "TDD 합시다")), Answer.class);

       getExchange(new URI(API_ANSWERS + "/1/answer/1"), HttpMethod.DELETE, emptyHttp(), Answer.class);

       List<Answer> answers = answerRepository.findAllByQuestionId(1L);

       softly.assertThat(answers.get(0).isDeleted()).isTrue();
    }
}
