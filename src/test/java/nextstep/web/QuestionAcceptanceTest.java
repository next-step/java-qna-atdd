package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.domain.UserRepository;
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

import javax.persistence.EntityNotFoundException;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인_없이_질문하기() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "제목입니다.")
                .addParameter("contents", "질문 내용")
                .build();

        ResponseEntity<String> response = template().postForEntity("/questions", request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 로그인_후_질문하기() throws Exception {
        User loginUser = defaultUser();
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "제목입니다.")
                .addParameter("contents", "질문 내용")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void 질문_리스트() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Question question = questionRepository.findAll().get(0);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains(question.generateUrl());
        softly.assertThat(response.getBody()).contains(question.getTitle());
    }

    @Test
    public void 질문_조회() throws Exception {
        Question question = questionRepository.findAll().get(0);
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", question.getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains(question.getTitle());
        softly.assertThat(response.getBody()).contains(question.generateUrl());
    }

    @Test
    public void 로그인_없이_수정폼_접근() throws Exception {
        Question question = questionRepository.findAll().get(0);
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", question.getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 다른_사용자_수정폼_접근() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/questions/%d/form", 2L), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 로그인_사용자_수정폼_접근() throws Exception {
        Question question = questionRepository.findById(1L).orElseThrow(EntityNotFoundException::new);
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/questions/%d/form", 1L), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(question.getTitle());
    }

    @Test
    public void 로그인_없이_수정() throws Exception {
        Question question = questionRepository.findAll().get(0);
        ResponseEntity<String> response = update(template(), question.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인_후_수정() throws Exception {
        Question question = questionRepository.findAll().get(0);
        Long id = question.getId();
        ResponseEntity<String> response = update(basicAuthTemplate(), question.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
        softly.assertThat(questionRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new)
                .getTitle()).isEqualTo("제목수정");
    }

    private ResponseEntity<String> update(TestRestTemplate template, Long id) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "put")
                .addParameter("title", "제목수정")
                .addParameter("contents", "수정내용입니다.")
                .build();

        return template.postForEntity(String.format("/questions/%d", id), request, String.class);
    }

    @Test
    public void 로그인_없이_삭제() throws Exception {
        Question question = questionRepository.findAll().get(0);
        ResponseEntity<String> response = delete(template(), question.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인_후_삭제() throws Exception {
        Long id = 1L;
        ResponseEntity<String> response = delete(basicAuthTemplate(), id);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
        softly.assertThat(questionRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new)
                .isDeleted()).isTrue();
    }

    private ResponseEntity<String> delete(TestRestTemplate template, Long id) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "delete")
                .build();

        return template.postForEntity(String.format("/questions/%d", id), request, String.class);
    }
}
