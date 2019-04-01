package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

public class QnaAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QnaAcceptanceTest.class);

    @Autowired private QuestionRepository questionRepository;

    @Test
    public void 질문_상세를_출력한다() {
        questionRepository.save(new Question("This is title", "This is contents"));

        ResponseEntity<String> response = template().getForEntity(
            String.format("/qna/%d", 1), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains("This is contents");
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 없는_질문이면_404가_발생한다() {
        ResponseEntity<String> response = template().getForEntity(
            String.format("/qna/%d", 1), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인한_사용자만_질문_등록폼에_들어갈수_있다() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/qna/form", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains("제목");
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인하지_않으면_사용자만_질문_등록폼에_들어갈수_없다() {
        ResponseEntity<String> response = template().getForEntity("/qna/form", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 질문을_등록할수_있다() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
            .addParameter("title", "This is title")
            .addParameter("content", "This is content")
            .build();

        ResponseEntity<String> response = basicAuthTemplate().postForEntity(
            "/qna", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }
}
