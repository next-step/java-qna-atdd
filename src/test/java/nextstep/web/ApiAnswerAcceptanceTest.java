package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private String questionLocaton;

    /*
    @Before
    public void setUp() {
        Question payload = new Question("This is title", "This is contents");
        ResponseEntity<Void> createResponse = createResource("/api/questions", payload, defaultUser());

        questionLocaton = createResponse.getHeaders().getLocation().getPath();
    }

    @Test
    public void 답변을_등록한다() {
        Answer answer = new Answer(defaultUser(), "This is answer");
        ResponseEntity<Void> createResponse = createResource(questionLocaton + "/answers", answer, defaultUser());

        String location = createResponse.getHeaders().getLocation().getPath();
        Answer createdAnswer = getResource(location, Answer.class, defaultUser()).getBody();

        softly.assertThat(createdAnswer.getContents()).isEqualTo("This is answer");
    }
    */
}
