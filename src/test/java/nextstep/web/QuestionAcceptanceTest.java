package nextstep.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Test
    public void 질문_상세를_출력한다() {
        ResponseEntity<String> response = template().getForEntity(
            String.format("/questions/%d", 1), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains("자바지기");
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인한_사용자만_질문_등록폼에_들어갈수_있다() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/form", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains("제목");
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인하지_않은_사용자는_질문_등록폼에_들어갈수_없다() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인한_사용자만_질문을_등록할수_있다() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
            .addParameter("title", "This is title")
            .addParameter("contents", "This is contents")
            .build();

        ResponseEntity<String> response = basicAuthTemplate().postForEntity(
            "/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인하지_않은_사용자는_질문을_등록할수_없다() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
            .addParameter("title", "This is title")
            .addParameter("contents", "This is contents")
            .build();

        ResponseEntity<String> response = template().postForEntity(
            "/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 질문_수정폼은_작성자만_들어갈수있다() {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity(
            String.format("/questions/%d/form", 1), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains("제목");
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 작성자가_아니면_질문_수정폼에_들어갈수없다() {
        ResponseEntity<String> response = basicAuthTemplate(findByUserId("sanjigi")).getForEntity(
            String.format("/questions/%d/form", 1), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("body : {}", response.getBody());
    }


    @Test
    public void 질문_수정은_작성자만_할수있다() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
            .addParameter("title", "This is updated title")
            .addParameter("contents", "This is updated contents")
            .build();

        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).exchange(
            String.format("/questions/%d", 1), HttpMethod.PATCH, request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 작성자가_아니면_질문_수정을_할수없다() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
            .addParameter("title", "This is updated title")
            .addParameter("contents", "This is updated contents")
            .build();

        ResponseEntity<String> response = basicAuthTemplate(findByUserId("sanjigi")).exchange(
            String.format("/questions/%d", 1), HttpMethod.PATCH, request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 질문_삭제는_작성자만_할수있다() {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).exchange(
            String.format("/questions/%d", 1), HttpMethod.DELETE, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 작성자가_아니면_질문_삭제를_할수없다() {
        ResponseEntity<String> response = basicAuthTemplate(findByUserId("sanjigi")).exchange(
            String.format("/questions/%d", 1), HttpMethod.DELETE, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("body : {}", response.getBody());
    }
}
