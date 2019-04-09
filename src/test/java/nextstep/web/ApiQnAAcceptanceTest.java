package nextstep.web;

import nextstep.domain.*;
import nextstep.dto.ListResponse;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.RestApiTestCaller;
import support.test.AcceptanceTest;

public class ApiQnAAcceptanceTest extends AcceptanceTest {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;

    private RestApiTestCaller restApiTestCaller;

    @Before
    public void setUp() {
        restApiTestCaller = new RestApiTestCaller(template());
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
        // when
        QuestionBody payload = new QuestionBody("This is title", "This is contents");
        ResponseEntity<Void> response = restApiTestCaller.createResource("/api/questions", payload, defaultUser());

        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String location = response.getHeaders().getLocation().getPath();
        Question question = restApiTestCaller.getResource(location, Question.class, defaultUser()).getBody();
        softly.assertThat(question.getQuestionBody()).isEqualTo(payload);
    }

    @Test
    public void 질문_목록을_조회한다() {
        // given
        generateQuestion(defaultUser());
        generateQuestion(defaultUser());

        // when
        ResponseEntity<ListResponse<Question>> response = restApiTestCaller.getListResource(
            "/api/questions", Question.class, defaultUser());

        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ListResponse<Question> responseQuestions = response.getBody();
        softly.assertThat(responseQuestions.getCount()).isEqualTo(questionRepository.findByDeletedFalse().size());
    }

    @Test
    public void 질문_상세를_조회한다() {
        // given
        Question question = generateQuestion(defaultUser());

        // when
        ResponseEntity<Question> response = restApiTestCaller.getResource(
            String.format("/api/questions/%d", question.getId()), Question.class, defaultUser());

        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Question responseQuestion = response.getBody();
        softly.assertThat(responseQuestion.getQuestionBody()).isEqualTo(question.getQuestionBody());
    }

    @Test
    public void 질문을_수정한다() {
        // given
        Question question = generateQuestion(defaultUser());

        // when
        QuestionBody newPayload = new QuestionBody("This is updated title", "This is updated contents");
        ResponseEntity<Question> response = restApiTestCaller.updateResource(
            String.format("/api/questions/%d", question.getId()), newPayload, Question.class, defaultUser());

        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Question responseQuestion = response.getBody();
        softly.assertThat(responseQuestion.getQuestionBody()).isEqualTo(newPayload);
    }

    @Test
    public void 작성자가_어니면_질문을_수정할수_없다() {
        // given
        Question question = generateQuestion(defaultUser());

        // when
        QuestionBody newPayload = new QuestionBody("This is updated title", "This is updated contents");
        ResponseEntity<Question> response = restApiTestCaller.updateResource(
            String.format("/api/questions/%d", question.getId()), newPayload, Question.class, findByUserId("sanjigi"));

        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 질문을_삭제한다() {
        // given
        Question question = generateQuestion(defaultUser());

        // when
        ResponseEntity<Void> response =
            restApiTestCaller.deleteResource(String.format("/api/questions/%d", question.getId()), defaultUser());

        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(questionRepository.findById(question.getId()).get().isDeleted()).isTrue();
    }

    @Test
    public void 작성자가_어니면_질문을_삭제할수_없다() {
        // given
        Question question = generateQuestion(defaultUser());

        // when
        ResponseEntity<Void> response = restApiTestCaller.deleteResource(
            String.format("/api/questions/%d", question.getId()), findByUserId("sanjigi"));

        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 질문에_답변을_등록한다() {
        // given
        Question question = generateQuestion(defaultUser());

        // when
        String contents = "This is answer";
        ResponseEntity<Void> response = restApiTestCaller.createResource(
            String.format("/api/questions/%d/answers", question.getId()), contents, defaultUser());

        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();
        Answer responseAnswer = restApiTestCaller.getResource(location, Answer.class, defaultUser()).getBody();
        softly.assertThat(responseAnswer.getContents()).isEqualTo(contents);
    }

    @Test
    public void 질문의_답변들을_조회한다() {
        // given
        Question question = generateQuestion(defaultUser());
        generateAnswer(defaultUser(), question);
        generateAnswer(defaultUser(), question);

        // when
        ResponseEntity<ListResponse<Answer>> response = restApiTestCaller.getListResource(
            String.format("/api/questions/%d/answers", question.getId()), Answer.class, defaultUser());

        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ListResponse<Answer> responseAnswers = response.getBody();
        softly.assertThat(responseAnswers.getCount()).isEqualTo(answerRepository.findByQuestionAndDeletedFalse(question).size());
    }

    @Test
    public void 질문의_답변_한건을_조회한다() {
        // given
        Question question = generateQuestion(defaultUser());
        Answer answer = generateAnswer(defaultUser(), question);

        // when
        ResponseEntity<Answer> response = restApiTestCaller.getResource(
            String.format("/api/questions/%d/answers/%d", question.getId(), answer.getId()), Answer.class, defaultUser());

        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Answer responseAnswer = response.getBody();
        softly.assertThat(responseAnswer.getContents()).isEqualTo(responseAnswer.getContents());
    }

    @Test
    public void 질문의_답변을_삭제한다() {
        // given
        Question question = generateQuestion(defaultUser());
        Answer answer = generateAnswer(defaultUser(), question);

        // when
        ResponseEntity<Void> response = restApiTestCaller.deleteResource(
            String.format("/api/questions/%d/answers/%d", question.getId(), answer.getId()), defaultUser());

        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(answerRepository.findById(answer.getId()).get().isDeleted()).isTrue();
    }
}
