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

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void question_read_no_login() {
        Question question = defaultQuestion();

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
                .addParameter("title", title)
                .addParameter("contents", contents)
                .build();

        ResponseEntity<String> response = template().postForEntity("/questions", request, String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void question_create_login() {
        // given
        User loginUser = defaultUser();
        String title = "Hello";
        String contents = "World";

        // when
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", title)
                .addParameter("contents", contents)
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", request, String.class);

        // then
        long idOfCreatedQuestion = questionRepository.findAll().stream().mapToLong(Question::getId).max().getAsLong();
        Question createdQuestion = questionRepository.findById(idOfCreatedQuestion).get();

        softly.assertThat(createdQuestion.getTitle()).isEqualTo(title);
        softly.assertThat(createdQuestion.getContents()).isEqualTo(contents);
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/questions/" + idOfCreatedQuestion);
    }

    @Test
    public void question_delete_no_login() {
        // given
        Question question = defaultQuestion();

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
        Question question = defaultQuestion();

        // when
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity(String.format("/questions/%d", question.getId()), request, String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
        softly.assertThat(questionRepository.findById(question.getId()).get().isDeleted()).isTrue();

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_delete_login_작성자가_아닐_경우() {
        // given
        User loginUser = defaultUser();
        Question otherQuestion = otherQuestion();

        // when
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity(String.format("/questions/%d", otherQuestion.getId()), request, String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.FORBIDDEN);
        softly.assertThat(questionRepository.findById(otherQuestion.getId()).get().isDeleted()).isFalse();

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_update_form_no_login() {
        // given
        Question question = defaultQuestion();

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
        Question question = defaultQuestion();

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
        User loginUser = defaultUser();
        Question otherQuestion = otherQuestion();

        // when
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/questions/%d/form", otherQuestion.getId()), String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.FORBIDDEN);

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_update_no_login() {
        // given
        Question question = defaultQuestion();
        String originalTitle = question.getTitle();
        String originalContents = question.getContents();
        String updatedTitle = "Hello";
        String updatedContents = "World";

        // when
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", updatedTitle)
                .addParameter("contents", updatedContents)
                .build();

        ResponseEntity<String> response = template()
                .postForEntity(String.format("/questions/%d", question.getId()), request, String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
        softly.assertThat(questionRepository.findById(question.getId()).get().getTitle()).isEqualTo(originalTitle);
        softly.assertThat(questionRepository.findById(question.getId()).get().getContents()).isEqualTo(originalContents);

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_update_login_작성자() {
        // given
        User loginUser = defaultUser();
        Question question = defaultQuestion();
        String updatedTitle = "Hello";
        String updatedContents = "World";

        // when
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", updatedTitle)
                .addParameter("contents", updatedContents)
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity(String.format("/questions/%d", question.getId()), request, String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo(question.generateUrl());
        softly.assertThat(questionRepository.findById(question.getId()).get().getTitle()).isEqualTo(updatedTitle);
        softly.assertThat(questionRepository.findById(question.getId()).get().getContents()).isEqualTo(updatedContents);

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_update_login_작성자_아닐_경우() {
        // given
        User loginUser = defaultUser();
        Question question = otherQuestion();
        String originalTitle = question.getTitle();
        String originalContents = question.getContents();
        String updatedTitle = "Hello";
        String updatedContents = "World";

        // when
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", updatedTitle)
                .addParameter("contents", updatedContents)
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity(String.format("/questions/%d", question.getId()), request, String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.FORBIDDEN);
        softly.assertThat(questionRepository.findById(question.getId()).get().getTitle()).isEqualTo(originalTitle);
        softly.assertThat(questionRepository.findById(question.getId()).get().getContents()).isEqualTo(originalContents);

        log.debug("response body : {}", response.getBody());
    }

    private Question otherQuestion() {
        return questionRepository.findById(2L).get();
    }
}
