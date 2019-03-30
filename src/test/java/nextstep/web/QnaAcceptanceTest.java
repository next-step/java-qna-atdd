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
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class QnaAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void showQuestion() {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/show/%d", 1L), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void createQuestionForm_no_login() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void createQuestionForm_login() {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void newQuestion_save() {
        User loginUser = defaultUser();
        Question question = new Question("question title", "question contents");
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.addParameter("userId", loginUser.getUserId());
        builder.addParameter("name", loginUser.getName());
        builder.addParameter("email", loginUser.getEmail());
        builder.addParameter("title", question.getTitle());
        builder.addParameter("contents", question.getContents());

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/questions/register", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void updateQuestion() {
        User loginUser = defaultUser();
        Question question = new Question("question title", "question contents update");
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
//        builder.put();
        builder.addParameter("userId", loginUser.getUserId());
        builder.addParameter("name", loginUser.getName());
        builder.addParameter("email", loginUser.getEmail());
        builder.addParameter("title", question.getTitle());
        builder.addParameter("contents", question.getContents());

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(String.format("/questions/update/%d", 1L), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void deleteQuestion() {
        User loginUser = defaultUser();
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.delete();
        builder.addParameter("userId", loginUser.getUserId());
        builder.addParameter("name", loginUser.getName());
        builder.addParameter("email", loginUser.getEmail());

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        newQuestion_save();

        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(String.format("/questions/delete/%d", 3L), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

}