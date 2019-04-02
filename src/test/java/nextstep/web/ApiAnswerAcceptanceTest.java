package nextstep.web;

import java.util.stream.StreamSupport;
import nextstep.domain.Answer;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    @Test
    public void list() {
        final long questionId = 1;

        ResponseEntity<Iterable> response =
            basicAuthTemplate().getForEntity(String.format("/api/questions/%d/answers", questionId), Iterable.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Iterable<Answer> answers = response.getBody();
        softly.assertThat(StreamSupport.stream(answers.spliterator(), false).count())
            .isGreaterThanOrEqualTo(0);
    }
}