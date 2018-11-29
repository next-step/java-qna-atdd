package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    }

    @Test
    public void 내_질문이_아니면_수정할_수_없다() {

    }

    @Test
    public void 내_질문_삭제() {

    }

    @Test
    public void 내_질문이_아니면_삭제할_수_없다() {

    }
}
