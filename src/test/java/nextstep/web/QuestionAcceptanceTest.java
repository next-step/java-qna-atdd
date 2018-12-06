package nextstep.web;

import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.WebAcceptanceTest;

import static support.util.MultiValueMapBuilder.builder;

public class QuestionAcceptanceTest extends WebAcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm_login() throws Exception {
        User loginUser = defaultUser();

        //when
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity("/questions/form", String.class);

        //then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void createForm_no_login() {
        //when
        ResponseEntity<String> response = template()
                .getForEntity("/questions/form", String.class);
        //then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void createQuestion_no_login() {
        ResponseEntity<String> response = template()
                .postForEntity("/questions", null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void createQuestion() {

        //given
        User loginUser = defaultUser();
        String title = "동해물과백두산이";
        String contents = "contents::동해물과백두산이";
        MultiValueMap<String, Object> params = builder()
                .add("title", title)
                .add("contents", contents)
                .build();
        HttpEntity request = createWebRequestEntity(params);

        //when
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", request, String.class);

        //then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void list() {
        String title = defaultQuestion().getTitle();

        ResponseEntity<String> response = template().getForEntity("/questions", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(title);
    }

    @Test
    public void modifyForm_login() {
        User loginUser = defaultUser();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity("/questions/updateForm", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void modifyForm_no_login() {
        ResponseEntity<String> response = template().getForEntity("/questions/updateForm", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void modifyQuestion() {

        String title = "동해물과백두산이";
        String contents = "contents::동해물과백두산이";

        MultiValueMap<String, Object> params = builder()
                .add("title", title)
                .add("contents", contents)
                .build();
        HttpEntity request = createWebRequestEntity(params);

        long questionId = defaultQuestion().getId();
        User loginUser = defaultQuestionWriter();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .exchange(String.format("/questions/%d", questionId), HttpMethod.PUT, request, String.class);

        //then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void modifyQuestion_no_login() throws Exception {
        long questionId = defaultQuestion().getId();

        ResponseEntity<String> response = template()
                .exchange(String.format("/questions/%d", questionId), HttpMethod.PUT, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void deleteQuestion() throws Exception {
        long questionId = defaultQuestion().getId();
        User loginUser = defaultQuestionWriter();
        log.info("loginUser::{}", loginUser.toString());
        log.info("defaultQuestion()::{}", defaultQuestion());

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .exchange(String.format("/questions/%d", questionId), HttpMethod.DELETE, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findByDeleted(true)).contains(defaultQuestion());
    }

    @Test
    public void deleteQuestion_no_login() {
        long questionId = defaultQuestion().getId();
        ResponseEntity<String> response = template()
                .exchange(String.format("/questions/%d", questionId), HttpMethod.DELETE, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
