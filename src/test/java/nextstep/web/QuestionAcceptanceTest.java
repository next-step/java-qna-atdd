package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.helper.HtmlFormDataBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

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
        softly.assertThat(questionRepository.findById(Long.valueOf(3)).isPresent()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

}
