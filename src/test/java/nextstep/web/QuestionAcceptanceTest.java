package nextstep.web;

import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.web.lib.HtmlFormDataBuilder;
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

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    //TODO : QnA CRUD 기능 ATDD 테스트
    //TODO : Read -> Create -> Delete -> Update 순서로 진행

    //1. 목록보기 (get) : /qna
    //2. 상세보기 (get) : /qna/{id}

    //3. 등록하기 (post) : /qna
    //4. 삭제하기 (delete) : /qna/{id}
    //5. 수정하기 (put) : /qna/{id}

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void home_qnaList() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/", String.class);

        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?");
    }

    @Test
    public void show() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/1", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create_form_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create_form_login() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity("/questions/form", String.class);

        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    //create_no_login
    @Test
    public void create_no_login() throws Exception {
        ResponseEntity<String> response = create(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void create_login() throws Exception {
        ResponseEntity<String> response = create(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    private ResponseEntity<String> create(TestRestTemplate template) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .post()
                .addParameter("title", "ATDD는 어떻게하면 좋을까요?")
                .addParameter("contents", "이렇게 하는게 맞는거겠죠?")
                .build();
        return template.postForEntity("/questions", request, String.class);
    }

    @Test
    public void delete_no_login() throws Exception {
        ResponseEntity<String> response = delete(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("{}", response.getBody());
    }

    @Test
    public void delete_login_owner() {
        ResponseEntity<String> response = delete(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug("{}", response.getBody());
    }

    private ResponseEntity<String> delete(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();
        return template.postForEntity(String.format("/questions/%d", defaultQuestion().getId()), request, String.class);
    }

    @Test
    public void updateForm_no_login() {
        ResponseEntity<String> response = template().getForEntity("/questions/1/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void updateForm_login() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/1/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void update_no_login() {
        ResponseEntity<String> response = update2(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void update_login() {
        ResponseEntity<String> response = update2(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> update2(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "제목수정")
                .addParameter("contents", "내용도 수정")
                .build();

        return template.postForEntity(String.format("/questions/%d", defaultQuestion().getId()), request, String.class);
    }
}
