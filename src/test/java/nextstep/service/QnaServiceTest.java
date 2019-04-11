package nextstep.service;

import nextstep.UnAuthenticationException;
import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import nextstep.dto.AnswerDTO;
import nextstep.dto.QuestionDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import support.test.BaseTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QnaServiceTest extends BaseTest {

    @Autowired
    private QnaService qnaService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private DeleteHistoryRepository deleteHistoryRepository;

    private long testQuestionId;
    private long testAnswerId;
    private long noAnswerQuestionId;
    private long anotherUserAnswerId;

    @Before
    public void setUp() throws Exception {
        Question question = new Question("질문하기", "테스트");
        User user = new User("sanjigi", "test", "name", "javajigi@slipp.net");
        user = userService.login(user.getUserId(), user.getPassword());
        Question result = qnaService.create(user, question);
        testQuestionId = result.getId();

        user = userService.login(user.getUserId(), user.getPassword());
        AnswerDTO answer = qnaService.addAnswer(user, testQuestionId, "answer test");
        testAnswerId = answer.getId();

        Question noAnswerQuestion = new Question("질문하기", "테스트");
        user = userService.login(user.getUserId(), user.getPassword());
        Question noAnswer = qnaService.create(user, noAnswerQuestion);
        noAnswerQuestionId = noAnswer.getId();
    }

    @After
    public void tearDown() {
        deleteHistoryRepository.deleteAll();
        answerRepository.deleteAll();
        questionRepository.deleteAll();
    }

    /*@Test
    public void create() throws UnAuthenticationException {
        Question question = new Question("질문하기", "테스트");
        User user = new User("sanjigi", "test", "name", "javajigi@slipp.net");
        user = userService.login(user.getUserId(), user.getPassword());
        Question result = qnaService.create(user, question);
        softly.assertThat(result.getTitle()).isEqualTo(question.getTitle());
    }*/

    @Test
    public void update() throws UnAuthenticationException {
        Question question = new Question("질문하기", "테스트");
        User user = new User("sanjigi", "test", "name", "javajigi@slipp.net");
        user = userService.login(user.getUserId(), user.getPassword());
        question.writeBy(user);
        QuestionDTO result = qnaService.update(user, testQuestionId, question);
        softly.assertThat(result.getContents()).isEqualTo(question.getContents());
    }

    @Test
    public void findAll() {
        Iterable<Question> result = qnaService.findAll();
        softly.assertThat(result).size().isGreaterThanOrEqualTo(1);
    }

    @Test
    public void findAllWithPageable() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Question> result = qnaService.findAll(pageRequest);
        softly.assertThat(result).size().isGreaterThanOrEqualTo(1);
    }

    @Test
    public void findQuestionWithAnswer() {
        QuestionDTO questionDTO = qnaService.findQuestionAndAnswerById(testQuestionId);
        softly.assertThat(questionDTO.getAnswerSize()).isEqualTo(1);
    }

    @Test
    public void findById() {
        Question result = qnaService.findById(testQuestionId);
        softly.assertThat(result.getWriter().getUserId()).isEqualTo("sanjigi");
    }

    @Test
    public void deleteQuestion() throws Exception {
        User user = new User("sanjigi", "test", "name", "javajigi@slipp.net");
        user = userService.login(user.getUserId(), user.getPassword());
        LocalDateTime createDate = LocalDateTime.now();
        QuestionDTO question = qnaService.deleteQuestion(user, testQuestionId, createDate);

        String updateAt = question.getUpdateAt();
        String createAt = question.getDeleteHistories().get(0).getCreateDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));

        softly.assertThat(updateAt).isEqualTo(createAt);
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestion_no_answer_owner() throws UnAuthenticationException {
        User user = new User("javajigi", "test", "name", "javajigi@slipp.net");
        user = userService.login(user.getUserId(), user.getPassword());
        AnswerDTO answer = qnaService.addAnswer(user, testQuestionId, "answer test");
        anotherUserAnswerId = answer.getId();

        user = new User("sanjigi", "test", "name", "javajigi@slipp.net");
        user = userService.login(user.getUserId(), user.getPassword());
        Question question = qnaService.findById(testQuestionId);
        LocalDateTime createDate = LocalDateTime.now();
        qnaService.deleteQuestion(user, question.getId(), createDate);
    }

    @Test
    public void deleteNoAnswerQuestion() throws UnAuthenticationException {
        User user = new User("sanjigi", "test", "name", "javajigi@slipp.net");
        user = userService.login(user.getUserId(), user.getPassword());
        Question question = qnaService.findById(noAnswerQuestionId);
        LocalDateTime createDate = LocalDateTime.now();
        qnaService.deleteQuestion(user, question.getId(), createDate);
    }

    @Test
    public void addAnswer() throws UnAuthenticationException {
        Question question = qnaService.findById(testQuestionId);
        User user = new User("sanjigi", "test", "name", "javajigi@slipp.net");
        user = userService.login(user.getUserId(), user.getPassword());
        AnswerDTO result = qnaService.addAnswer(user, question.getId(), "answer test");
        softly.assertThat(result.getContents()).isEqualTo("answer test");
    }

    @Test
    public void deleteAnswer() throws UnAuthenticationException {
        User user = new User("sanjigi", "test", "name", "javajigi@slipp.net");
        user = userService.login(user.getUserId(), user.getPassword());
        Answer answer = qnaService.findAnswerById(testAnswerId);
        LocalDateTime createDate = LocalDateTime.now();
        AnswerDTO answerDTO = qnaService.deleteAnswer(user, answer.getId(), createDate);

        String updateAt = answerDTO.getUpdateAt();
        String createAt = answerDTO.getHistories().get(0).getCreateDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));

        softly.assertThat(updateAt).isEqualTo(createAt);
    }
}