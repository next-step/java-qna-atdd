package nextstep.web;

import nextstep.domain.QuestionRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    QuestionRepository repository;

    @Test
    public void 질문수정_성공() {
        String title = "테스트 제목";
        String contents = "테스트 수 글";

        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title",title)
                .addParameter("contents", contents)
                .build();

        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions/1", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/questions/1");
    }

    @Test
    public void 질문수정_작성자아님_실패() {
        String title = "테스트 제목";
        String contents = "테스트 수 글";

        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title",title)
                .addParameter("contents", contents)
                .build();

        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions/2", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @Transactional
    public void 질문삭제_댓글존재_실패() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method","delete")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity("/questions/1", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 질문삭제_작성자아님_실패() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method","delete")
                .build();

        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions/2", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
