package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.helper.HtmlFormDataBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import javax.persistence.Temporal;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);
    private static final long DEFAULT_QUESTION_ID2 = 2;

    @Autowired
    private QuestionRepository questionRepository;
    @Test
    public void list() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains(defaultQuestion().getTitle());
    }

    @Test
    public void create() {
        String title = "제목임";
        User loginUser = defaultUser();

        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParam("title", title)
                .addParam("contents", "가나다라")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath().startsWith("/questions/"));
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {
        String id = String.valueOf(defaultQuestion().getId());
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParam("id", id)
                .addParam("title", "제목수정")
                .addParam("contents", "본문수정")
                .build();
        return template.postForEntity(String.format("/questions/%s", id), request, String.class);
    }

    @Test
    public void update() throws Exception {
        ResponseEntity<String> response = update(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()
                .startsWith(String.format("/questions/%d",defaultQuestion().getId())));
    }

    @Test
    public void findByID() {
        User loginUser = defaultUser();
        String id = String.valueOf(defaultQuestion().getId());
        ResponseEntity<String> response = find(loginUser, id);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().contains(defaultQuestion().getTitle()));
    }

    private ResponseEntity<String> find(User loginUser, String id) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .get()
                .build();

        return basicAuthTemplate(loginUser).getForEntity(String.format("/questions/%s", id), String.class);
    }


    @Test
    public void delete() {
        User loginUser = defaultUser();
        String id = String.valueOf(3L);
        ResponseEntity<String> response = delete(loginUser, id);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    private ResponseEntity<String> delete(User loginUser, String id) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();

        return basicAuthTemplate(loginUser).postForEntity(String.format("/questions/%s", id), request, String.class);
    }


}
