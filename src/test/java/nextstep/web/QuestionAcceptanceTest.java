package nextstep.web;

import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.domain.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        User loginUser = defaultUser();
        String title = "첫 게시물";
        String contents = "첫 내용";

        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", loginUser.getUserId())
                .addParameter("title", title)
                .addParameter("contents", contents)
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", request, String.class);
        
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void list() throws Exception {
        PageRequest pageRequest = PageRequest.of(1, 10);
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/list", pageRequest),  String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void updateForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", defaultUser().getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "제목수정한 것")
                .addParameter("contents", "내용 수정된 것")
                .build();

        return template.postForEntity(String.format("/questions/%d", defaultQuestion().getId()), request, String.class);
    }

    @Test
    public void update() throws Exception {
        ResponseEntity<String> response = update(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void delete() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/questions/delete/%d", defaultQuestion().getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug("body : {}", response.getBody());
    }
}
