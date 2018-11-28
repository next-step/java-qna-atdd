package nextstep.web;

import nextstep.domain.Question;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import java.net.URI;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private final String API_QUESIONS = "/api/questions";

    @Test
    public void create() throws Exception {
        URI location = LoginUserCreate(API_QUESIONS);
        Question question = getResource(location, Question.class, defaultUser());

        softly.assertThat(question).isNotNull();
    }

    @Test
    public void create_not_login() throws Exception {
        ResponseEntity<Question> response = getExchangeNotLogin(new URI(API_QUESIONS ), HttpMethod.POST, createHttp(new Question("TDD를 배우는 이유는?", "리팩토링 향상을 위해")), Question.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    public void update() throws Exception {
        URI location = LoginUserCreate(API_QUESIONS);
        Question updateQuestion = new Question("TDD", "리팩토링 향상");
        ResponseEntity<Question> updateResponse = getExchange(location, HttpMethod.PUT, createHttp(updateQuestion), Question.class);
        Question responseQuestion = updateResponse.getBody();

        softly.assertThat(updateQuestion).isEqualTo(responseQuestion);
    }

    @Test
    public void update_orher_login() throws Exception {
        URI location = LoginUserCreate(API_QUESIONS);
        Question updateQuestion = new Question("TDD", "리팩토링 향상");
        ResponseEntity<Question> updateResponse = getExchangeUser(findByUserId("sanjigi"), location, HttpMethod.PUT, createHttp(updateQuestion), Question.class);

        softly.assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete(){
        URI location = LoginUserCreate(API_QUESIONS);
        ResponseEntity<Void> respoonse = getExchange(location, HttpMethod.DELETE, emptyHttp(), Void.class);

        softly.assertThat(respoonse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private URI LoginUserCreate(String url) {
        Question question = new Question("TDD를 배우는 이유는?", "리팩토링 향상을 위해");
        question.writeBy(defaultUser());

        ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).postForEntity(url, question,  Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return response.getHeaders().getLocation();
    }

}
