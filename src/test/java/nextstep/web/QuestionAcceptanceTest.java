package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import java.util.Collections;

public class QuestionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void 질문_조회() {
        Question question = questionRepository.findById(1L).get();

        ResponseEntity<String> response = template().getForEntity("/questions/" + question.getId(), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(question.getTitle());
    }

    @Test
    public void 질문하기_로그인_유저() {
        ResponseEntity<String> response = createQuestion(basicAuthTemplate());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void 비로그인_유저는_질문할_수_없다() {
        ResponseEntity<String> response = createQuestion(template());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 내_질문_수정() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("_method", "put");
        params.add("title", "질문이 있습니다");
        params.add("contents", "답변해주세요.");

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = basicAuthTemplate().postForEntity(String.format("/questions/%d", 1), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith(String.format("/questions/%d", 1));
    }

    @Test
    public void 내_질문이_아니면_수정할_수_없다() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("_method", "put");
        params.add("title", "질문이 있습니다");
        params.add("contents", "답변해주세요.");

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = basicAuthTemplate(findByUserId("sanjigi")).postForEntity(String.format("/questions/%d", 1), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 내_질문_삭제() {

    }

    @Test
    public void 내_질문이_아니면_삭제할_수_없다() {

    }

    private ResponseEntity<String> createQuestion(TestRestTemplate template) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("title", "질문이 있습니다");
        params.add("contents", "답변해주세요.");

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);

        return template.postForEntity("/questions", request, String.class);
    }
}
