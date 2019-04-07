package nextstep.service;

import nextstep.UnAuthenticationException;
import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
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

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QnaServiceTest extends BaseTest {

    @Autowired
    private QnaService qnaService;

    @Autowired
    private UserService userService;

    private long testQuestionId;
    private long testAnswerId;

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
        Question result = qnaService.update(user, testQuestionId, question);
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
        QuestionDTO questionDTO = qnaService.findQuestionAndAnswerById(1);
        softly.assertThat(questionDTO.getAnswerSize()).isEqualTo(2);
    }

    @Test
    public void findById() {
        Question result = qnaService.findById(testQuestionId);
        softly.assertThat(result.getWriter().getUserId()).isEqualTo("sanjigi");
    }

    /*@Test
    public void deleteQuestion() throws UnAuthenticationException {
        User user = new User("sanjigi", "test", "name", "javajigi@slipp.net");
        user = userService.login(user.getUserId(), user.getPassword());
        Question question = qnaService.findById(testQuestionId);
        qnaService.deleteQuestion(user, question.getId());
    }*/

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
        qnaService.deleteAnswer(user, answer.getId());
    }

    @After
    public void tearDown() throws Exception {
        User user = new User("sanjigi", "test", "name", "javajigi@slipp.net");
        user = userService.login(user.getUserId(), user.getPassword());
        qnaService.deleteQuestion(user, testQuestionId);
    }
}