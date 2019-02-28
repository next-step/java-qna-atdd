package nextstep.web;

import nextstep.domain.*;
import nextstep.service.QnaService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import javax.transaction.Transactional;
import java.util.List;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private static final String QUESTION_API = "/api/questions";
    private static final String ANSWER_API_CONTEXT = QUESTION_API + "/{questionId}/answers";

    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QnaService qnaService;

    @Test
    public void add() {
        // 1. 로그인
        User loginUser = defaultUser();

        softly.assertThat(loginUser).isNotNull();

        // 2. 질문 작성
        Question question = new Question("title", "contents");
        ResponseEntity<Question> questionWriteResponse = basicAuthTemplate().postForEntity("/api/questions", question, Question.class);
        String questionResource = questionWriteResponse.getHeaders().getLocation().getPath();
        Question origin = getResource(questionResource, Question.class, loginUser);

        softly.assertThat(origin).isNotNull();

        // 3. 작성된 질문에 답변을 달기
        Answer answer = new Answer(loginUser, "this is answer");
        ResponseEntity<Answer> answerWriteResponse = post(answer, basicAuthTemplate(), origin.getId());

        softly.assertThat(answerWriteResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // 4. 답변을 추가한 후 확인을 위해 질문을 다시 가져옴
        Question addedQuestion = getResource(questionResource, Question.class, loginUser);
        Answer addedAnswer = addedQuestion.getAnswer(0);

        softly.assertThat(addedAnswer.equals(answerWriteResponse.getBody())).isTrue();
    }

    private ResponseEntity<Answer> post(Answer answer, TestRestTemplate testRestTemplate, Long id) {
        return testRestTemplate.postForEntity("/api/questions/{id}/answers", answer, Answer.class, id);
    }

    @Test
    @Transactional
    public void show() throws Exception {
        Question question = questionRepository.findById(1L).get();
        Answer answer = template().getForObject("/api/questions/" + questionRepository.findById(1L).get().getId() + "/answers", Answer.class);
        softly.assertThat(answer).isEqualTo(question.getAnswer(0));
    }

    @Test
    @Transactional
    public void update() throws Exception {
        // 1. 질문 작성
        Question question = new Question("title", "contents");
        ResponseEntity<Question> questionWriteResponse = basicAuthTemplate().postForEntity("/api/questions", question, Question.class);
        String questionResource = questionWriteResponse.getHeaders().getLocation().getPath();
        Question origin = getResource(questionResource, Question.class, defaultUser());

        // 2. 답변 추가
        Answer answer = new Answer(defaultUser(), "original answer");
        ResponseEntity<Answer> answerWriteResponse =  post(answer, basicAuthTemplate(), origin.getId());
        String answerResource = answerWriteResponse.getHeaders().getLocation().getPath();

        // 3. 답변 수정
        String modifyContents = "modify answer";
        Answer updatedAnswer = new Answer(defaultUser(), modifyContents);
        ResponseEntity<Answer> response = basicAuthTemplate().exchange(answerResource, HttpMethod.PUT, createHttpEntity(updatedAnswer), Answer.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().getContents().equals(modifyContents)).isTrue();
    }

    @Test
    public void update_다른사용자() throws Exception {
        // 1. 질문 작성
        Question question = new Question("title", "contents");
        ResponseEntity<Question> questionWriteResponse = basicAuthTemplate().postForEntity("/api/questions", question, Question.class);
        String questionResource = questionWriteResponse.getHeaders().getLocation().getPath();
        Question origin = getResource(questionResource, Question.class, defaultUser());

        // 2. 답변 추가
        Answer answer = new Answer(defaultUser(), "original answer");
        ResponseEntity<Answer> answerWriteResponse =  post(answer, basicAuthTemplate(), origin.getId());
        String answerResource = answerWriteResponse.getHeaders().getLocation().getPath();

        // 3. (다른유저가) 답변 수정
        String modifyContents = "modify answer";
        Answer updatedAnswer = new Answer(UserTest.SANJIGI, modifyContents);
        ResponseEntity<Answer> response = basicAuthTemplate(UserTest.SANJIGI).exchange(answerResource, HttpMethod.PUT, createHttpEntity(updatedAnswer), Answer.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", response.getBody());
    }

    @Test
    public void delete() throws Exception {
        // 1. 질문 작성
        Question question = new Question("title", "contents");
        ResponseEntity<Question> questionWriteResponse = basicAuthTemplate().postForEntity("/api/questions", question, Question.class);
        String questionResource = questionWriteResponse.getHeaders().getLocation().getPath();
        Question origin = getResource(questionResource, Question.class, defaultUser());

        // 2. 답변 추가
        Answer answer = new Answer(defaultUser(), "original answer");
        ResponseEntity<Answer> answerWriteResponse =  post(answer, basicAuthTemplate(), origin.getId());
        String answerResource = answerWriteResponse.getHeaders().getLocation().getPath();

        // 3. 답변 삭제
        ResponseEntity<Answer> response = basicAuthTemplate().exchange(answerResource, HttpMethod.DELETE, createHttpEntity(answer), Answer.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_다른사용자() throws Exception {
        // 1. 질문 작성
        Question question = new Question("title", "contents");
        ResponseEntity<Question> questionWriteResponse = basicAuthTemplate().postForEntity("/api/questions", question, Question.class);
        String questionResource = questionWriteResponse.getHeaders().getLocation().getPath();
        Question origin = getResource(questionResource, Question.class, defaultUser());

        // 2. 답변 추가
        Answer answer = new Answer(defaultUser(), "original answer");
        ResponseEntity<Answer> answerWriteResponse =  post(answer, basicAuthTemplate(), origin.getId());
        String answerResource = answerWriteResponse.getHeaders().getLocation().getPath();

        // 3. 다른유저가 답변 삭제
        ResponseEntity<Answer> response = basicAuthTemplate(UserTest.SANJIGI).exchange(answerResource, HttpMethod.DELETE, createHttpEntity(answer), Answer.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}