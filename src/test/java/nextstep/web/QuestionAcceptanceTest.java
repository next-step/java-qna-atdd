package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HttpHelper;

public class QuestionAcceptanceTest extends AcceptanceTest {

    private String createdUrl;

    private Question defaultQuestion = QuestionTest.newQuestion();

    @Before
    public void setUp() throws Exception {
        ResponseEntity<String> response = create(basicAuthTemplate());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        createdUrl = response.getHeaders().getLocation().getPath();
    }

    @Test
    public void 질문하기_로그인_유저() {
        ResponseEntity<String> response = HttpHelper.get(template(), createdUrl);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(defaultQuestion.getTitle());
    }

    @Test
    public void 비로그인_유저는_질문할_수_없다() {
        ResponseEntity<String> response = create(template());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 내_질문_수정() {
        ResponseEntity<String> response = update(basicAuthTemplate());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith(createdUrl);
    }

    @Test
    public void 내_질문이_아니면_수정할_수_없다() {
        ResponseEntity<String> response = update(basicAuthTemplate(findByUserId("sanjigi")));

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 내_질문_삭제() {
        ResponseEntity<String> response = HttpHelper.delete(basicAuthTemplate(), createdUrl);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");

        softly.assertThat(HttpHelper.get(template(), createdUrl).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 내_질문이_아니면_삭제할_수_없다() {
        ResponseEntity<String> response = HttpHelper.delete(basicAuthTemplate(findByUserId("sanjigi")), createdUrl);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<String> create(TestRestTemplate template) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("title", defaultQuestion.getTitle());
        params.add("contents", defaultQuestion.getContents());

        return HttpHelper.post(template, "/questions", params);
    }

    private ResponseEntity<String> update(TestRestTemplate template) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("title", "질문이 있습니다");
        params.add("contents", "답변해주세요.");

        return HttpHelper.put(template, createdUrl, params);
    }
}
