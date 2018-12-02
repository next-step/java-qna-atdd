package nextstep.web;

import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
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

    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() {
        User loginUser = defaultUser();
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "제목_Test")
                .addParameter("contents", "내용_Test_@@")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains("제목_Test", "내용_Test_@@");
    }

    @Test
    public void list() {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains("/questions/1");
    }

    public ResponseEntity<String> questionUpdateResponse(TestRestTemplate response, String title, String contents) {
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.urlEncodedForm().put()
                .addParameter("title", title)
                .addParameter("contents", contents)
                .build();
        return response.postForEntity("/questions/1", request, String.class);
    }

    @Test
    public void updateQuestion_owner() {
        String title = "질문 제목 수정 TEST", contents = "질문 내용 수정 TEST !!";
        ResponseEntity<String> response = questionUpdateResponse(basicAuthTemplate(), title, contents);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(title, contents);
    }

    @Test
    public void updateQuestion_not_owner() {
        User loginUser = findByUserId("sanjigi");
        String title = "질문 제목 수정 TEST", contents = "질문 내용 수정 TEST !!";
        ResponseEntity<String> response = questionUpdateResponse(basicAuthTemplate(loginUser), title, contents);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void deleteQuestion_owner() {
        User loginUser = defaultUser();
        basicAuthTemplate(loginUser).delete("/questions/1");
        softly.assertThat(questionRepository.findByDeleted(true)).hasSize(1);
    }

    @Test
    public void deleteQuestion_not_owner() {
        User loginUser = findByUserId("sanjigi");
        basicAuthTemplate(loginUser).delete("/questions/1");
        softly.assertThat(questionRepository.findByDeleted(true)).hasSize(0);
    }

    @Test
    public void add_answer() {
        User loginUser = findByUserId("sanjigi");
        long questionId = 2;
        String contents = "답글 테스트";
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.urlEncodedForm()
                .addParameter("contents", contents)
                .build();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/answers", request, String.class);

    }
}
