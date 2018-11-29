package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void 질문_조회() {
        Question existing = questionRepository.findById(1L).get();

        Question question = template().getForObject("/api" + existing.generateUrl(), Question.class);

        softly.assertThat(question.getTitle()).isEqualTo(existing.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(existing.getContents());
    }

    @Test
    public void 질문하기_로그인_유저() {
        Question question = new Question("질문하기", "질문 내용");
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", question, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        Question retrieved = template().getForObject(location, Question.class);

        softly.assertThat(retrieved.getTitle()).isEqualTo(question.getTitle());
        softly.assertThat(retrieved.getContents()).isEqualTo(question.getContents());
    }

    @Test
    public void 비로그인_유저는_질문할_수_없다() {
        Question question = new Question("질문하기", "질문 내용");
        ResponseEntity<Void> response = template().postForEntity("/api/questions", question, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 내_질문_수정() {
        Question original = template().getForObject("/api/questions/1", Question.class);

        Question updateQuestion = new Question("질문수정", "수정했어요");

        ResponseEntity<Question> response = basicAuthTemplate(original.getWriter())
            .exchange("/api" + original.generateUrl(), HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().getTitle()).isEqualTo(updateQuestion.getTitle());
        softly.assertThat(response.getBody().getContents()).isEqualTo(updateQuestion.getContents());
    }

    @Test
    public void 내_질문이_아니면_수정할_수_없다() {
        Question original = template().getForObject("/api/questions/1", Question.class);

        Question updateQuestion = new Question("질문수정", "수정했어요");

        ResponseEntity<Question> response = basicAuthTemplate(findByUserId("sanjigi"))
            .exchange("/api" + original.generateUrl(), HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 내_질문_삭제() {
        Question target = questionRepository.findById(1L).get();

        HttpEntity<Object> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<Void> response = basicAuthTemplate(target.getWriter())
            .exchange("/api" + target.generateUrl(), HttpMethod.DELETE, entity, Void.class);


        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        softly.assertThat(questionRepository.findById(target.getId()).get().isDeleted()).isTrue();
    }

    @Test
    public void 내_질문이_아니면_삭제할_수_없다() {
        Question target = questionRepository.findById(1L).get();

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
