package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    private static final String TITLE = "title";
    private static final String CONTENTS = "contents";

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void question_read_no_login() {
        Question question = questionOfDefaultUser();

        // when
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", question.getId()), String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_create_form_no_login() {
        // when
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_create_form_login() {
        // given
        User loginUser = defaultUser();

        // when
        ResponseEntity<String> response = basicAuthTemplate(loginUser).getForEntity("/questions/form", String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_create_no_login() {
        // given
        String title = "Hello";
        String contents = "World";

        // when
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter(TITLE, title)
                .addParameter(CONTENTS, contents)
                .build();

        ResponseEntity<String> response = template().postForEntity("/questions", request, String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_create_login() {
        // given
        User loginUser = defaultUser();
        String title = "Hello";
        String contents = "World";

        // when
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter(TITLE, title)
                .addParameter(CONTENTS, contents)
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", request, String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.FOUND);

        String location = response.getHeaders().getLocation().getPath();
        long idOfCreatedQuestion = getIdFromResourceLocation(location);

        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/questions/" + idOfCreatedQuestion);

        Question dbQuestion = questionRepository.findById(idOfCreatedQuestion).get();

        softly.assertThat(dbQuestion.getTitle()).isEqualTo(title);
        softly.assertThat(dbQuestion.getContents()).isEqualTo(contents);

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_delete_no_login() {
        // given
        Question question = questionOfDefaultUser();

        // when
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();

        ResponseEntity<String> response = template()
                .postForEntity(String.format("/questions/%d", question.getId()), request, String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_delete_login_작성자() {
        // given
        User loginUser = defaultUser();
        Question question = questionOfDefaultUser();

        // when
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity(String.format("/questions/%d", question.getId()), request, String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");

        Question dbAnswer = questionRepository.findById(question.getId()).get();
        softly.assertThat(dbAnswer.isDeleted()).isTrue();

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_update_form_no_login() {
        // given
        Question question = questionOfDefaultUser();

        // when
        ResponseEntity<String> response = template()
                .getForEntity(String.format("/questions/%d/form", question.getId()), String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_update_form_login_작성자() {
        // given
        User loginUser = defaultUser();
        Question question = questionOfDefaultUser();

        // when
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/questions/%d/form", question.getId()), String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_update_form_login_작성자_아닐_경우() {
        // given
        User loginUser = otherUser();
        Question question = questionOfDefaultUser();

        // when
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/questions/%d/form", question.getId()), String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.FORBIDDEN);

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_update_no_login() {
        // given
        Question question = questionOfDefaultUser();
        String updatedTitle = "Hello";
        String updatedContents = "World";

        // when
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter(TITLE, updatedTitle)
                .addParameter(CONTENTS, updatedContents)
                .build();

        ResponseEntity<String> response = template()
                .postForEntity(String.format("/questions/%d", question.getId()), request, String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);


        Question dbQuestion = questionRepository.findById(question.getId()).get();
        softly.assertThat(dbQuestion.getTitle()).isNotEqualTo(updatedTitle);
        softly.assertThat(dbQuestion.getContents()).isNotEqualTo(updatedContents);

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_update_login_작성자() {
        // given
        User loginUser = defaultUser();
        Question question = questionOfDefaultUser();
        String updatedTitle = "Hello";
        String updatedContents = "World";

        // when
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter(TITLE, updatedTitle)
                .addParameter(CONTENTS, updatedContents)
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity(String.format("/questions/%d", question.getId()), request, String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo(question.generateUrl());

        Question dbQuestion = questionRepository.findById(question.getId()).get();
        softly.assertThat(dbQuestion.getTitle()).isEqualTo(updatedTitle);
        softly.assertThat(dbQuestion.getContents()).isEqualTo(updatedContents);

        log.debug("response body : {}", response.getBody());
    }

    private Question questionOfDefaultUser() {
        User loginUser = defaultUser();

        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter(TITLE, "Netty")
                .addParameter(CONTENTS, "Socket")
                .build();

        ResponseEntity<String> response =
                basicAuthTemplate(loginUser).postForEntity("/questions", request, String.class);

        String createQuestionResourcelocation = response.getHeaders().getLocation().getPath();
        long id = getIdFromResourceLocation(createQuestionResourcelocation);

        return questionRepository.findById(id).get();
    }

    private long getIdFromResourceLocation(String createQuestionResourcelocation) {
        return Long.parseLong(createQuestionResourcelocation.split("/questions/")[1]);
    }
}
