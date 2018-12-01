package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import javax.xml.ws.Response;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private String location;

    @Before
    public void setUp() throws Exception {
        Question question = defaultQuestion();
        Answer answer = new Answer(defaultUser(), "내용");
        location = createResource("/api/questions/" + question.getId() + "/answers", answer, defaultUser());
    }

    @Test
    public void 생성() throws Exception {
        softly.assertThat(getResource(location, Question.class)).isNotNull();
    }

    @Test
    public void 수정() {
        String contents = "수정된 내용";
        ResponseEntity<Answer> responseEntity = basicAuthTemplate()
                .exchange(location, HttpMethod.PUT, createHttpEntity(new Answer(defaultUser(), contents)), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(getResource(location, Answer.class, defaultUser()).getContents()).isEqualTo(contents);
    }

    @Test
    public void 삭제() {
        Answer original = getResource(location, Answer.class, defaultUser());
        ResponseEntity<Void> responseEntity = basicAuthTemplate()
                .exchange(location, HttpMethod.DELETE, createHttpEntity(original.getId()), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(basicAuthTemplate().getForObject(location, Question.class)).isNull();
    }

    @Test
    public void 삭제_다른_사용자() {
        Answer original = getResource(location, Answer.class, defaultUser());
        ResponseEntity<Void> responseEntity = basicAuthTemplate(findByUserId("sanjigi"))
                .exchange(location, HttpMethod.DELETE, createHttpEntity(original.getId()), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
