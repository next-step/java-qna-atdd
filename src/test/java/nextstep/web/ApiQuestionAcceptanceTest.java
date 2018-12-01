package nextstep.web;

import nextstep.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static nextstep.domain.QuestionTest.CONTENTS;
import static nextstep.domain.QuestionTest.TITLE;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    private static final String API_QUESTIONS = "/api/questions";

    private Question newQuestion;
    private Question updateQuestion;

    @Before
    public void setup() {
        newQuestion = new Question(TITLE, CONTENTS);
        updateQuestion = new Question("질문 수정제목 입니다.", "질문은 수정질문입니다.");
    }

    @Test
    public void show() {
        ResponseEntity<String> response = template().getForEntity(String.format(API_QUESTIONS + "/%d", 1L), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() {
        String location = createResource(API_QUESTIONS, newQuestion, basicAuthTemplate());
        final Question createdQuestion = getResource(location, Question.class, defaultUser());
        softly.assertThat(createdQuestion).isNotNull();
    }


    @Test
    public void create_not_login() {
        ResponseEntity<Void> response = template().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    public void update_writer_login() {
        String location = createResource(API_QUESTIONS, newQuestion, basicAuthTemplate());
        ResponseEntity<String> response = updateResource(location, updateQuestion, basicAuthTemplate());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void update_login() {
        String location = createResource(API_QUESTIONS, newQuestion, basicAuthTemplate());
        ResponseEntity<String> response = updateResource(location, updateQuestion, basicAuthTemplate(UserTest.SANJIGI));

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void update_not_login() {
        String location = createResource(API_QUESTIONS, newQuestion, basicAuthTemplate());
        ResponseEntity<String> response = updateResource(location, updateQuestion, template());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_writer_login() {
        String location = createResource(API_QUESTIONS, newQuestion, basicAuthTemplate());
        ResponseEntity<String> response = deleteResource(location, basicAuthTemplate());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void delete_login() {
        String location = createResource(API_QUESTIONS, newQuestion, basicAuthTemplate());
        ResponseEntity<String> response = deleteResource(location, basicAuthTemplate(UserTest.SANJIGI));

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_not_login() {
        String location = createResource(API_QUESTIONS, newQuestion, basicAuthTemplate());
        ResponseEntity<String> response = deleteResource(location, template());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
