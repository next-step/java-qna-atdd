package nextstep.web;

import com.sun.javafx.binding.StringFormatter;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import java.util.Optional;

import static org.mockito.Mockito.when;

public class QnaAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QnaAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void list() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void detail() throws Exception {
        Question question = createTestQuestion();

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        ResponseEntity<String> response = template().getForEntity("/qna/" + 1, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains("article-header-text");
    }

    private Question createTestQuestion() {
        User loginUser = defaultUser();
        Question question = new Question("test title", "test contents");
        question.writeBy(loginUser);

        return question;
    }

    @Test
    public void create_form() throws Exception {
        User loginUser = defaultUser();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                                .getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains("/questions");
    }

    @Test
    public void create() throws Exception {
        User loginUser = defaultUser();
        String title = "test title";
        String contents = "test contents";
        HttpEntity<MultiValueMap<String, Object>> request
                = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", title)
                .addParameter("contents", contents)
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findFirstByTitleAndContents(title, contents).isPresent()).isTrue();
    }

    @Test
    public void update_form() throws Exception {
        User loginUser = defaultUser();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/questions/%d/form", 1), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains("/questions");
    }

    @Test
    public void delete() throws Exception {
        User loginUser = defaultUser();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .exchange(String.format("/questions/%d", 1), HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }
}
