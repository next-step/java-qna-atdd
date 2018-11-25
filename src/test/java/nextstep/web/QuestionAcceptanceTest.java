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
import support.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm_login() throws Exception {
        User loginUser = defaultUser();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity("/questions/form", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void createForm_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void create_login() throws Exception {
        String title = "제목1";
        User loginUser = defaultUser();

        HttpEntity request = HtmlFormDataBuilder
                .urlEncodedForm()
                .addParameter("title", title)
                .addParameter("contents", "내용1")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findTopByTitle(title).isPresent()).isTrue();
    }

    @Test
    public void create_no_login() throws Exception {
        ResponseEntity<String> response = template().postForEntity("/questions", null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void list() throws Exception {
        String title = defaultQuestion().getTitle();

        ResponseEntity<String> response = template().getForEntity("/questions", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(title);
    }

    @Test
    public void updateForm_login() throws Exception {
        User loginUser = defaultUser();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity("/questions/updateForm", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void updateForm_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/updateForm", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_login() throws Exception {
        long questionId = defaultQuestion().getId();
        User loginUser = defaultQuestionWriter();
        String title = "제목2";

        HttpEntity request = HtmlFormDataBuilder
                .urlEncodedForm()
                .addParameter("title", title)
                .addParameter("contents", "내용2")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .exchange(String.format("/questions/%d", questionId), HttpMethod.PUT, request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findTopByTitle(title).isPresent()).isTrue();
    }

    @Test
    public void update_no_login() throws Exception {
        long questionId = defaultQuestion().getId();

        ResponseEntity<String> response = template().exchange(String.format("/questions/%d", questionId), HttpMethod.PUT, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() throws Exception {
        long questionId = defaultQuestion().getId();
        User loginUser = defaultQuestionWriter();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .exchange(String.format("/questions/%d", questionId), HttpMethod.DELETE, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findByDeleted(true)).contains(defaultQuestion());
    }

    @Test
    public void delete_no_login() throws Exception {
        long questionId = defaultQuestion().getId();

        ResponseEntity<String> response = template().exchange(String.format("/questions/%d", questionId), HttpMethod.DELETE, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
