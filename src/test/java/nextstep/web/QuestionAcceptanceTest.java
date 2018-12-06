package nextstep.web;

import nextstep.domain.Question;
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
import support.domain.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);


    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Test
    //질문 폼 생성하기!
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> create(TestRestTemplate template) throws Exception {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();

        htmlFormDataBuilder.addParameter("title", "question_one");
        htmlFormDataBuilder.addParameter("contents", "질문있습니다!");

        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
        return template.postForEntity("/questions", request, String.class);
    }

    @Test
    public void create_login() throws Exception {
        ResponseEntity<String> response = create(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");

    }

    @Test
    public void create_noLogin() throws Exception {
        ResponseEntity<String> response = create(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());

    }

    @Test
    public void qnaList() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains(defaultQuestion().getTitle());
    }

    @Test
    public void qnaListDetail() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/detail", defaultQuestion().getId()), String.class);
        softly.assertThat(response.getBody()).contains(defaultQuestion().getTitle());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    public void updateForm() throws Exception {
        Question question = defaultQuestion();
        ResponseEntity<String> response = template()
                .getForEntity(String.format("/questions/%d/form", question.getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(defaultQuestion().getTitle());

    }

    @Test
    public void update_qna_login() throws Exception {
        ResponseEntity<String> response = update(basicAuthTemplate());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");

    }

    @Test
    public void update_qna_no_login() throws Exception {
        ResponseEntity<String> response = update(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        htmlFormDataBuilder.addParameter("_method", "put");
        htmlFormDataBuilder.addParameter("title", "new");
        htmlFormDataBuilder.addParameter("contents", "변경된내용입니다.");
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();

        return template.postForEntity(String.format("/questions/%d", defaultQuestion().getId()), request, String.class);

    }

    @Test
    public void delete_qna_no_login() throws Exception {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
        ResponseEntity<String> response = template().postForEntity(String.format("/questions/%d/delete", defaultQuestion().getId()), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_qna_login() throws Exception {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity(String.format("/questions/%d/delete", defaultQuestion().getId()), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);


    }
}
