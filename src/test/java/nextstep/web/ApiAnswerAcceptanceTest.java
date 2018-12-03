package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.UserTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    private static final String API_QUESTIONS = "/api/questions";
    private static final String API_ANSWERS = "/1/answers";

    public static final String ANSWER_CONTENTS = "답변 컨텐츠입니다.";

    @Test
    public void create() {
        ResponseEntity<Void> resource = createResource(API_QUESTIONS + API_ANSWERS, ANSWER_CONTENTS, basicAuthTemplate());
        String location = resource.getHeaders().getLocation().getPath();

        final Question createdQuestion = getResource(location, Question.class, defaultUser());
        softly.assertThat(createdQuestion).isNotNull();
    }


    @Test
    public void create_not_login() {
        ResponseEntity<Void> response = createResource(API_QUESTIONS + API_ANSWERS, ANSWER_CONTENTS, template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    public void delete_writer_login() {
        ResponseEntity<Void> response = createResource(API_QUESTIONS + API_ANSWERS, ANSWER_CONTENTS, basicAuthTemplate());
        String location = response.getHeaders().getLocation().getPath();

        ResponseEntity<String> deleteResource = deleteResource(location, basicAuthTemplate(), String.class);
        softly.assertThat(deleteResource.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void delete_login() {
        ResponseEntity<Void> response = createResource(API_QUESTIONS + API_ANSWERS, ANSWER_CONTENTS, basicAuthTemplate());
        String location = response.getHeaders().getLocation().getPath();

        ResponseEntity<String> deleteResource = deleteResource(location, basicAuthTemplate(UserTest.SANJIGI), String.class);
        softly.assertThat(deleteResource.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_not_login() {
        ResponseEntity<Void> response = createResource(API_QUESTIONS + API_ANSWERS, ANSWER_CONTENTS, basicAuthTemplate());
        String location = response.getHeaders().getLocation().getPath();

        ResponseEntity<String> deleteResource = deleteResource(location, template(), String.class);
        softly.assertThat(deleteResource.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
