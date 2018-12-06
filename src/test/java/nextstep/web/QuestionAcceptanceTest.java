package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Autowired
    private QuestionRepository qnaRepository;

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        htmlFormDataBuilder.addParameter("id", "3");
        htmlFormDataBuilder.addParameter("title", "제목정하기");
        htmlFormDataBuilder.addParameter("contents", "내용정하기");
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/questions", htmlFormDataBuilder.build(), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(qnaRepository.findById(3L).isPresent()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void show() throws Exception {
        Question question = qnaRepository.findById(3L).get();
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", question.getId()) ,String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains(question.getTitle());
    }

    @Test
    public void update() throws Exception {
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        htmlFormDataBuilder.addParameter("_method", "put");
        htmlFormDataBuilder.addParameter("title", "제목정하기");
        htmlFormDataBuilder.addParameter("contents", "내용정하기");
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(String.format("/questions/%d", 3L), htmlFormDataBuilder.build(), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void delete() throws Exception {
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        htmlFormDataBuilder.addParameter("_method", "delete");
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(String.format("/questions/%d", 1), htmlFormDataBuilder.build(), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }
}