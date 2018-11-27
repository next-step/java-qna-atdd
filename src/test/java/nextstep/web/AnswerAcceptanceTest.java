package nextstep.web;

import nextstep.AnswerNotFoundException;
import nextstep.domain.AnswerRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.Objects;

public class AnswerAcceptanceTest extends AcceptanceTest {
    @Autowired
    private AnswerRepository answerRepository;

    @Test
    public void createAnswer() {
        User loginUser = defaultUser();

        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("contents", "test contents")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions/answers/1", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(answerRepository.findById(1L).isPresent()).isTrue();
        softly.assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).getPath()).startsWith("/questions");
    }
    @Test
    public void updateForm_no_login() {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/answers/%d/form", 2L),
                String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void updateForm_login() {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/questions/answers/%d/form", 1L), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().contains("강추")).isTrue();
    }

    private ResponseEntity<String> update(TestRestTemplate template) {
        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("password", "test")
                .addParameter("contents", "국내에는 없다")
                .build();

        return template.postForEntity(String.format("/questions/answers/%d",1L), request, String.class);
    }

    @Test
    public void update_no_login() {
        ResponseEntity<String> response = update(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update() {
        ResponseEntity<String> response = update(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).getPath()).startsWith("/questions");
    }

    @Test
    public void delete() {
        User loginUser = defaultUser();

        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity(String.format("/questions/answers/%d", 1L), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(answerRepository.findById(1L).orElseThrow(AnswerNotFoundException::new).isDeleted()).isTrue();
        softly.assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).getPath()).startsWith("/questions");
    }
}
