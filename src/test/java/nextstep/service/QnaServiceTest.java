package nextstep.service;

import nextstep.UnAuthenticationException;
import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.dto.QuestionDTO;
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

    @Test
    public void create() throws UnAuthenticationException {
        Question question = new Question("질문하기", "테스트");
        User user = new User("sanjigi", "test", "name", "javajigi@slipp.net");
        user = userService.login(user.getUserId(), user.getPassword());
        Question result = qnaService.create(user, question);
        softly.assertThat(result.getTitle()).isEqualTo(question.getTitle());
    }

    @Test
    public void update() throws UnAuthenticationException {
        Question question = new Question("질문하기", "테스트");
        User user = new User("sanjigi", "test", "name", "javajigi@slipp.net");
        user = userService.login(user.getUserId(), user.getPassword());
        question.writeBy(user);
        Question result = qnaService.update(user, 2, question);
        softly.assertThat(result.getContents()).isEqualTo(question.getContents());
    }

    @Test
    public void findAll() throws UnAuthenticationException {
        create();
        Iterable<Question> result = qnaService.findAll();
        softly.assertThat(result).size().isGreaterThanOrEqualTo(1);
    }

    @Test
    public void findAllWithPageable() throws UnAuthenticationException {
        create();
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
        Question result = qnaService.findById(2);
        softly.assertThat(result.getWriter().getUserId()).isEqualTo("sanjigi");
    }

    @Test
    public void deleteQuestion() throws UnAuthenticationException {
        User user = new User("sanjigi", "test", "name", "javajigi@slipp.net");
        user = userService.login(user.getUserId(), user.getPassword());
        Question question = qnaService.findById(2);
        qnaService.deleteQuestion(user, question.getId());
    }

    @Test
    public void addAnswer() throws UnAuthenticationException {
        Question question = qnaService.findById(2);
        User user = new User("sanjigi", "test", "name", "javajigi@slipp.net");
        user = userService.login(user.getUserId(), user.getPassword());
        Answer result = qnaService.addAnswer(user, question.getId(), "answer test");
        softly.assertThat(result.getContents()).isEqualTo("answer test");
    }

    @Test
    public void deleteAnswer() throws UnAuthenticationException {
        User user = new User("sanjigi", "test", "name", "javajigi@slipp.net");
        user = userService.login(user.getUserId(), user.getPassword());
        Answer answer = qnaService.findAnswerById(2);
        qnaService.deleteAnswer(user, answer.getId());
    }
}