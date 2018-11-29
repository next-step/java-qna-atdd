package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HttpHelper;

public class QuestionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void 질문_조회() {
        Question question = questionRepository.findById(1L).get();

        ResponseEntity<String> response = HttpHelper.get(template(), question.generateUrl());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(question.getTitle());
    }

    @Test
    public void 질문하기_로그인_유저() {
        ResponseEntity<String> response = create(basicAuthTemplate());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void 비로그인_유저는_질문할_수_없다() {
        ResponseEntity<String> response = create(template());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 내_질문_수정() {
        Question question = questionRepository.findById(1L).get();

        ResponseEntity<String> response = update(basicAuthTemplate(question.getWriter()));

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith(question.generateUrl());
    }

    @Test
    public void 내_질문이_아니면_수정할_수_없다() {
        ResponseEntity<String> response = update(basicAuthTemplate(findByUserId("sanjigi")));

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 내_질문_삭제() {
        ResponseEntity<String> response = HttpHelper.delete(basicAuthTemplate(), String.format("/questions/%d", 1));

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
        softly.assertThat(questionRepository.findById(1L).get().isDeleted()).isTrue();
    }

    @Test
    public void 내_질문이_아니면_삭제할_수_없다() {
        ResponseEntity<String> response = HttpHelper.delete(basicAuthTemplate(findByUserId("sanjigi")), String.format("/questions/%d", 1));

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<String> create(TestRestTemplate template) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("title", "질문이 있습니다");
        params.add("contents", "답변해주세요.");

        return HttpHelper.post(template, "/questions", params);
    }

    private ResponseEntity<String> update(TestRestTemplate template) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("title", "질문이 있습니다");
        params.add("contents", "답변해주세요.");

        return HttpHelper.put(template, String.format("/questions/%d", 1), params);
    }
}
