package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private String createdUrl;

    @Before
    public void setUp() throws Exception {
        createdUrl = createResource("/api/questions", QuestionTest.newQuestion(), defaultUser());
    }

    @Test
    public void 질문하기_로그인_유저() {
        Question created = getResource(createdUrl, Question.class, defaultUser());

        softly.assertThat(created.isEqualsTitleAndContents(QuestionTest.newQuestion())).isTrue();
    }

    @Test
    public void 비로그인_유저는_질문할_수_없다() {
        ResponseEntity<Void> response = template().postForEntity("/api/questions", QuestionTest.newQuestion(), Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 내_질문_수정() {
        Question original = getResource(createdUrl, Question.class, defaultUser());

        Question updateQuestion = new Question("질문수정", "수정했어요");

        ResponseEntity<Question> response = basicAuthTemplate(original.getWriter())
            .exchange("/api" + original.generateUrl(), HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().isEqualsTitleAndContents(updateQuestion)).isTrue();
    }

    @Test
    public void 내_질문이_아니면_수정할_수_없다() {
        Question original = getResource(createdUrl, Question.class, defaultUser());

        Question updateQuestion = new Question("질문수정", "수정했어요");

        ResponseEntity<Question> response = basicAuthTemplate(findByUserId("sanjigi"))
            .exchange("/api" + original.generateUrl(), HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 내_질문_삭제() {
        Question target = getResource(createdUrl, Question.class, defaultUser());

        HttpEntity<Object> entity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<Void> response = basicAuthTemplate(target.getWriter())
            .exchange("/api" + target.generateUrl(), HttpMethod.DELETE, entity, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        softly.assertThat(getResource(createdUrl, Question.class, defaultUser())).isNull();
    }

    @Test
    public void 내_질문이_아니면_삭제할_수_없다() {
        Question target = getResource(createdUrl, Question.class, defaultUser());

        HttpEntity<Object> entity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<Void> response = basicAuthTemplate(findByUserId("sanjigi"))
            .exchange("/api" + target.generateUrl(), HttpMethod.DELETE, entity, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
