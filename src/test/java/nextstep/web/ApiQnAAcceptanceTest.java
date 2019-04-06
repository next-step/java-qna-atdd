package nextstep.web;

import nextstep.domain.*;
import nextstep.dto.ListResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.Optional;

//@DataJpaTest
public class ApiQnAAcceptanceTest extends AcceptanceTest {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;

    @Test
    public void 질문을_등록한다() {
        // when
        QuestionBody payload = new QuestionBody("This is title", "This is contents");
        ResponseEntity<Void> createResponse = createResource("/api/questions", payload, defaultUser());

        // then
        String location = createResponse.getHeaders().getLocation().getPath();
        softly.assertThat(location).isEqualTo("/api/questions/1");
        Optional<Question> maybeCreatedQuestion = questionRepository.findById(1L);
        softly.assertThat(maybeCreatedQuestion).isNotEmpty();
    }

    @Test
    public void 질문_목록을_조회한다() {
        // given
        questionRepository.save(new Question(defaultUser(), new QuestionBody("This is title1", "This is contents1")));
        questionRepository.save(new Question(defaultUser(), new QuestionBody("This is title2", "This is contents2")));

        // when
        ListResponse<Question> result = getListResource(
            "/api/questions", Question.class, defaultUser()).getBody();

        // then
        softly.assertThat(result.getCount()).isEqualTo(2);
    }

    @Test
    public void 질문_상세를_조회한다() {
        // given
        questionRepository.save(new Question(defaultUser(), new QuestionBody("This is title1", "This is contents1")));

        // when
        Question question = getResource(String.format("/api/questions/%d", 1), Question.class, defaultUser()).getBody();

        // then
        softly.assertThat(question.getQuestionBody()).isNotNull();
    }

    @Test
    public void 질문을_수정한다() {
        // given
        questionRepository.save(new Question(defaultUser(), new QuestionBody("This is title1", "This is contents1")));

        // when
        QuestionBody newPayload = new QuestionBody("This is updated title", "This is updated contents");
        Question updatedQuestion = updateResource(String.format("/api/questions/%d", 1),
            newPayload, Question.class, defaultUser()).getBody();

        // then
        softly.assertThat(updatedQuestion.getQuestionBody()).isEqualTo(newPayload);
    }

    @Test
    public void 작성자가_어니면_질문을_수정할수_없다() {
        // given
        questionRepository.save(new Question(defaultUser(), new QuestionBody("This is title1", "This is contents1")));

        // when
        QuestionBody newPayload = new QuestionBody("This is updated title", "This is updated contents");
        ResponseEntity<Void> updateResponse = basicAuthTemplate(findByUserId("sanjigi")).exchange(
            String.format("/api/questions/%d", 1),
            HttpMethod.PUT,
            createHttpEntity(newPayload),
            Void.class);

        // then
        softly.assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 질문을_삭제한다() {
        // given
        questionRepository.save(new Question(defaultUser(), new QuestionBody("This is title1", "This is contents1")));

        // when
        deleteResource(String.format("/api/questions/%d", 1), defaultUser());

        // then
        Optional<Question> maybeDeletedQuestion = questionRepository.findById(1L);
        softly.assertThat(maybeDeletedQuestion).isEmpty();
    }

    @Test
    public void 작성자가_어니면_질문을_삭제할수_없다() {
        // given
        questionRepository.save(new Question(defaultUser(), new QuestionBody("This is title1", "This is contents1")));

        // when
        ResponseEntity<Void> deleteResponse = basicAuthTemplate(findByUserId("sanjigi")).exchange(
            String.format("/api/questions/%d", 1),
            HttpMethod.DELETE,
            null,
            Void.class);

        // then
        softly.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 질문에_답변을_등록한다() {
        // given
        questionRepository.save(new Question(defaultUser(), new QuestionBody("This is title", "This is contents")));

        // when
        String contents = "This is answer";
        ResponseEntity<Void> createResponse = createResource("/api/questions/1/answers", contents, defaultUser());

        // then
        String location = createResponse.getHeaders().getLocation().getPath();
        softly.assertThat(location).isEqualTo("/api/questions/1/answers/1");
        Optional<Answer> maybeCreatedAnswer = answerRepository.findById(1L);
        softly.assertThat(maybeCreatedAnswer).isNotEmpty();
    }

    @Test
    public void 질문의_답변들을_조회한다() {
        // given
        questionRepository.save(new Question(defaultUser(), new QuestionBody("This is title", "This is contents")));
//        answerRepository.save(new Answer())
    }
}
