package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.domain.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import java.util.Optional;

import static nextstep.domain.QuestionTest.newQuestion;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    static final String TITLE = "제목 내용";
    static final String CONTENTS = "본문 내용";
    static final String API_ANSWER_LOCATION = "/api/answers";
    static final String API_QUESTION_LOCATION = "/api/questions";


    @Test
    public void show() throws Exception {
        String location = createLocation();
        Answer answer = basicAuthTemplate().getForObject(location, Answer.class);

        softly.assertThat(answer).isNotNull();
    }

    @Test
    public void add() throws Exception {
        String location = createLocation();
        String contents = "댓글 내용";

        ResponseEntity<Answer> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(contents), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete() throws Exception {
        String location = createLocation();

        ResponseEntity<Answer> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.DELETE, createHttpEntity(""), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private String createLocation() {
        final User loginUser = defaultUser();
        String location = createResource(API_ANSWER_LOCATION, newQuestion(TITLE, CONTENTS, loginUser));
        createResource(API_QUESTION_LOCATION, newQuestion(TITLE, CONTENTS, loginUser));
        return location;
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
