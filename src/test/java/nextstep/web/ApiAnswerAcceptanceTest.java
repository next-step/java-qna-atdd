package nextstep.web;

import nextstep.domain.Answer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;


public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);


    //create
    @Test
    public void create_withLogin() {

        Answer newAnswer = new Answer(defaultUser(), "이것은 답변입니다.");
        ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).postForEntity("/api/answers/questions/1", newAnswer, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    }

    @Test
    public void update_withLogin() {
        String newContents = "update Answers";
        Answer newAnswer = new Answer(defaultUser(), newContents);
        String location = createResource("/api/answers/1", newAnswer.getContents());
        ResponseEntity<Answer> responseEntity = basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.PUT, createHttpEntity(newContents), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        //   softly.assertThat(newQuestion.isOwner(defaultUser())).isTrue();


    }

    @Test
    public void delete_withLogin() {
        basicAuthTemplate(defaultUser()).delete("/api/answers/1");
        Answer answer = getResource("/api/answers/1", Answer.class, defaultUser());
        softly.assertThat(answer.isDeleted()).isTrue();
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }

}
