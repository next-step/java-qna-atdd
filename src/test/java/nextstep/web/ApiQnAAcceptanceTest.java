package nextstep.web;

import nextstep.domain.*;
import nextstep.dto.ListResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.Random;

public class ApiQnAAcceptanceTest extends AcceptanceTest {
    @Autowired private UserRepository userRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private AnswerRepository answerRepository;

    private User generateUser() {
//        return userRepository.save(new User("userId", "userPass", "userName", "javajigi@slipp.net"));
        return defaultUser();
    }

    private Question generateQuestion(User writer) {
        return questionRepository.save(
            new Question(writer, new QuestionBody("This is title", "This is contents")));
    }

    private Answer generateAnswer(User writer, Question question) {
        return answerRepository.save(new Answer(writer, question, "This is answer"));
    }

    @Test
    public void 질문을_등록한다() {
        // given
        User givenUser = generateUser();

        // when
        QuestionBody payload = new QuestionBody("This is title", "This is contents");
        ResponseEntity<Void> createResponse = createResource("/api/questions", payload, givenUser);

        // then
        String location = createResponse.getHeaders().getLocation().getPath();
        Question question = getResource(location, Question.class, givenUser).getBody();
        softly.assertThat(question.getQuestionBody()).isEqualTo(payload);
    }

    @Test
    public void 질문_목록을_조회한다() {
        // given
        User givenUser = generateUser();
        generateQuestion(givenUser);
        generateQuestion(givenUser);

        // when
        ListResponse<Question> result = getListResource(
            "/api/questions", Question.class, givenUser).getBody();

        // then
        softly.assertThat(result.getCount()).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void 질문_상세를_조회한다() {
        // given
        User givenUser = generateUser();
        Question givenQuestion = generateQuestion(givenUser);

        // when
        Question question = getResource(String.format("/api/questions/%d", givenQuestion.getId()),
            Question.class, givenUser).getBody();

        // then
        softly.assertThat(question.getQuestionBody()).isNotNull();
    }

    @Test
    public void 질문을_수정한다() {
        // given
        User givenUser = generateUser();
        Question givenQuestion = generateQuestion(givenUser);

        // when
        QuestionBody newPayload = new QuestionBody("This is updated title", "This is updated contents");
        Question updatedQuestion = updateResource(String.format("/api/questions/%d", givenQuestion.getId()),
            newPayload, Question.class, givenUser).getBody();

        // then
        softly.assertThat(updatedQuestion.getQuestionBody()).isEqualTo(newPayload);
    }

    @Test
    public void 작성자가_어니면_질문을_수정할수_없다() {
        // given
        User givenUser = generateUser();
        Question givenQuestion = generateQuestion(givenUser);

        // when
        QuestionBody newPayload = new QuestionBody("This is updated title", "This is updated contents");
        ResponseEntity<Void> updateResponse = basicAuthTemplate(findByUserId("sanjigi")).exchange(
            String.format("/api/questions/%d", givenQuestion.getId()),
            HttpMethod.PUT, createHttpEntity(newPayload), Void.class);

        // then
        softly.assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 질문을_삭제한다() {
        // given
        User givenUser = generateUser();
        Question givenQuestion = generateQuestion(givenUser);

        // when
        deleteResource(String.format("/api/questions/%d", givenQuestion.getId()), givenUser);
    }

    @Test
    public void 작성자가_어니면_질문을_삭제할수_없다() {
        // given
        User givenUser = generateUser();
        Question givenQuestion = generateQuestion(givenUser);

        // when
        ResponseEntity<Void> deleteResponse = basicAuthTemplate(findByUserId("sanjigi")).exchange(
            String.format("/api/questions/%d", givenQuestion.getId()),
            HttpMethod.DELETE, null, Void.class);

        // then
        softly.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 질문에_답변을_등록한다() {
        // given
        User givenUser = generateUser();
        Question givenQuestion = generateQuestion(givenUser);

        // when
        String contents = "This is answer";
        ResponseEntity<Void> createResponse = createResource(
            String.format("/api/questions/%d/answers", givenQuestion.getId()), contents, givenUser);

        // then
        String location = createResponse.getHeaders().getLocation().getPath();
        Answer answer = getResource(location, Answer.class, givenUser).getBody();
        softly.assertThat(answer.getContents()).isEqualTo(contents);
    }

    @Test
    public void 질문의_답변들을_조회한다() {
        // given
        User givenUser = generateUser();
        Question givenQuestion = generateQuestion(givenUser);
        generateAnswer(givenUser, givenQuestion);
        generateAnswer(givenUser, givenQuestion);

        // when
        ListResponse<Answer> result = getListResource(
            String.format("/api/questions/%d/answers", givenQuestion.getId()), Answer.class, givenUser).getBody();

        // then
        softly.assertThat(result.getCount()).isEqualTo(2);
    }

    @Test
    public void 질문의_답변_한건을_조회한다() {
        // given
        User givenUser = generateUser();
        Question givenQuestion = generateQuestion(givenUser);
        Answer givenAnswer = generateAnswer(givenUser, givenQuestion);

        // when
        Answer answer = getResource(
            String.format("/api/questions/%d/answers/%d", givenQuestion.getId(), givenAnswer.getId()), Answer.class, givenUser).getBody();

        // then
        softly.assertThat(answer.getContents()).isEqualTo(givenAnswer.getContents());
    }
}
