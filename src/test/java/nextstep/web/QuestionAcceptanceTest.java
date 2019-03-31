package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);
    private Question firstQuestion;
    private Question secondQuestion;

    @Autowired
    private QuestionRepository questionRepository;

    @Before
    public void setUp() throws Exception {
        firstQuestion = questionRepository.findById(1L).get();
        secondQuestion = questionRepository.findById(2L).get();
    }

    @Test
    public void createForm_no_login() throws Exception {
        // given
        // when
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void createForm_login() throws Exception {
        // given
        // when
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/form", String.class);
        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> makeResponseEntityForCreate(User user) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "제목이요.")
                .addParameter("contents", "내용이요.")
                .build();
        return basicAuthTemplate(user).postForEntity("/questions", request, String.class);
    }

    @Test
    public void create() throws Exception {
        // given
        // when
        ResponseEntity<String> response = makeResponseEntityForCreate(defaultUser());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void create_no_login() throws Exception {
        // given
        // when
        ResponseEntity<String> response = makeResponseEntityForCreate(User.GUEST_USER);
        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void show() throws Exception {
        // given
        // when
        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity(String.format("/questions/%d", firstQuestion.getId()), String.class);
        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(firstQuestion.getTitle()).contains(firstQuestion.getContents());
    }

    @Test
    public void show_no_login() throws Exception {
        // given
        // when
        ResponseEntity<String> response = template()
                .getForEntity(String.format("/questions/%d", firstQuestion.getId()), String.class);
        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void show_not_exist() throws Exception {
        // given
        long notExistQuestionId = 0L;
        // when
        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity(String.format("/questions/%d", notExistQuestionId), String.class);
        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateForm_login() {
        // given
        // when
        ResponseEntity<String> response = basicAuthTemplate().getForEntity(String.format("/questions/%d/form", firstQuestion.getId()), String.class);
        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(firstQuestion.getTitle()).contains(firstQuestion.getContents());
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void updateForm_no_login() {
        // given
        // when
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", firstQuestion.getId()), String.class);
        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<String> makeResponseEntityForUpdate(User user) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "수정제목")
                .addParameter("contents", "수정내용")
                .build();
        return basicAuthTemplate(user)
                .postForEntity(String.format("/questions/%d", firstQuestion.getId()), request, String.class);
    }

    @Test
    public void update_owner() {
        // given
        // when
        ResponseEntity<String> response = makeResponseEntityForUpdate(firstQuestion.getWriter());
        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/questions/" + firstQuestion.getId());
        log.debug("question : {}", questionRepository.findById(1L).get());
    }

    @Test
    public void update_not_owner() {
        // given
        // when
        ResponseEntity<String> response = makeResponseEntityForUpdate(secondQuestion.getWriter());
        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<String> makeResponseEntityForDelete(User user) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().delete().build();
        return basicAuthTemplate(user)
                .postForEntity(String.format("/questions/%d", firstQuestion.getId()), request, String.class);
    }

    @Test
    public void delete_no_login() {
        // given
        // when
        ResponseEntity<String> response = makeResponseEntityForDelete(User.GUEST_USER);
        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_not_owner() {
        // given
        // when
        ResponseEntity<String> response = makeResponseEntityForDelete(secondQuestion.getWriter());
        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_owner() {
        // given
        // when
        ResponseEntity<String> response = makeResponseEntityForDelete(firstQuestion.getWriter());
        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
        softly.assertThat(questionRepository.findById(1L).get().isDeleted()).isTrue();
    }
}