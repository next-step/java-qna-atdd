package nextstep.web;

import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;


    @Test
    public void 로그인_안한_사용자_form_접속() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인_사용자_form_접속() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/form", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> createContents(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
            .post()
            .addParameter("title", "제목입니다.")
            .addParameter("contents", "내용입니다.")
            .build();

        return template.postForEntity("/questions/", request, String.class);
    }


    @Test
    public void 로그인_사용자_글작성() throws Exception {
        ResponseEntity<String> response = createContents(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인_안한_사용자_글작성() throws Exception {
        ResponseEntity<String> response = createContents(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> updateQuestion(TestRestTemplate template) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
            .put()
            .addParameter("title", "제목수정")
            .addParameter("contents", "내용수정")
            .build();

        return template.postForEntity(String.format("/questions/%d", 1), request, String.class);
    }

    @Test
    public void 로그인_한_사용자_글_업데이트() throws Exception {
        ResponseEntity<String> response = updateQuestion(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인_한_다른_사용자_글_업데이트() throws Exception {
        ResponseEntity<String> response = updateQuestion(basicAuthTemplate(findByUserId("testid")));
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인_안한_사용자_글_업데이트() throws Exception {
        ResponseEntity<String> response = updateQuestion(basicAuthTemplate(User.GUEST_USER));
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }


    private ResponseEntity<String> deleteQuestion(TestRestTemplate restTemplate, long id) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
            .delete()
            .build();
        return restTemplate.postForEntity(String.format("/questions/%d", id), request, String.class);
    }

    @Test
    public void 로그인_한_사용자_자신의_글삭제() throws Exception {
        ResponseEntity<String> response = deleteQuestion(basicAuthTemplate(), 4);
//        basicAuthTemplate(defaultUser()).delete("/questions/4");
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
        log.debug("body : {}", response.getBody());
//        softly.assertThat(questionRepository.findById(4L).get().isDeleted()).isTrue();
    }

    @Test
    public void 로그인_한_사용자_다른_사용자의_글삭제() throws Exception {
        ResponseEntity<String> response = deleteQuestion(basicAuthTemplate(findByUserId("testid")), 1);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인_안한_사용자_다른_사용자의_글삭제() throws Exception {
        ResponseEntity<String> response = deleteQuestion(basicAuthTemplate(User.GUEST_USER), 1);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }


    @Test
    public void 로그인_사용자_댓글_작성_가능() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
            .addParameter("댓글", "댓글")
            .build();

        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity("/questions/1/answers", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug("body : {}", request.getBody());

    }

    @Test
    public void 로그인_안한_사용자_댓글_작성_불가능() {
        HttpEntity<MultiValueMap<String, Object>> build = HtmlFormDataBuilder.urlEncodedForm()
            .addParameter("댓글", "댓글")
            .post()
            .build();
        ResponseEntity<String> responseEntity = basicAuthTemplate(User.GUEST_USER).postForEntity("/questions/1/answers", build, String.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", build.getBody());
    }

    @Test
    public void 자신의_댓글_삭제_가능() {
        HttpEntity<MultiValueMap<String, Object>> build = HtmlFormDataBuilder.urlEncodedForm()
            .delete()
            .build();
        ResponseEntity<String> responseEntity = basicAuthTemplate(defaultUser()).postForEntity("/questions/1/answers/3", build, String.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug("body : {}", build.getBody());
    }


    @Test
    public void 다른_유저_댓글_삭제_불가능() {
        HttpEntity<MultiValueMap<String, Object>> build = HtmlFormDataBuilder.urlEncodedForm()
            .delete()
            .build();
        ResponseEntity<String> responseEntity = basicAuthTemplate(User.GUEST_USER).postForEntity("/questions/1/answers/3", build, String.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", build.getBody());
    }


}
