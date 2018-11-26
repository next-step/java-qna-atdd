package nextstep.service;

import nextstep.UnAuthenticationException;
import nextstep.UnAuthorizedException;
import nextstep.domain.AnswerRepository;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class QnaServiceTest extends BaseTest {

    private QnaService qnaService;

    private QuestionRepository questionRepository = mock(QuestionRepository.class);
    private AnswerRepository answerRepository = mock(AnswerRepository.class);
    private DeleteHistoryService deleteHistoryService = mock(DeleteHistoryService.class);


    @Before
    public void setup() {
        this.qnaService = new QnaService(questionRepository, answerRepository, deleteHistoryService);
    }

    @Test
    public void findById() {
        final Long questionId = 1L;
        final Question question = new Question("title", "contents");
        final Optional<Question> expected = Optional.of(question);
        when(questionRepository.findById(questionId)).thenReturn(expected);


        final Optional<Question> questionOptional = qnaService.findById(questionId);


        softly.assertThat(questionOptional).isEqualTo(expected);
    }

    @Test
    public void findNotDeletedQuestionById() {
        final Long questionId = 1L;
        final Question question = new Question("title", "contents");
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(question));


        final Question findedQuestion = qnaService.findNotDeletedQuestionById(questionId);


        softly.assertThat(findedQuestion).isEqualTo(question);
    }

    @Test
    public void create() throws UnAuthenticationException {
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final Question mockQuestion = mock(Question.class);
        when(questionRepository.save(mockQuestion)).thenReturn(mockQuestion);


        final Question created = qnaService.create(loginUser, mockQuestion);


        verify(mockQuestion, times(1)).writeBy(loginUser);
        softly.assertThat(created).isEqualTo(mockQuestion);
    }

    @Test(expected = UnAuthenticationException.class)
    public void create_로그인_안한_사용자() throws UnAuthenticationException {
        final Question question = new Question("title", "contents");
        final User guest = User.GUEST_USER;


        qnaService.create(guest, question);
    }

    @Test
    public void update() throws UnAuthenticationException {
        final Long questionId = 1L;
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final Question question = new Question("title", "contents");
        final Question mockQuestion = mock(Question.class);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(mockQuestion));
        when(mockQuestion.update(loginUser, question)).thenReturn(mockQuestion);


        final Question update = qnaService.update(loginUser, questionId, question);


        verify(mockQuestion, times(1)).update(loginUser, question);
        softly.assertThat(update).isEqualTo(mockQuestion);
    }

    @Test(expected = EntityNotFoundException.class)
    public void update_질문_아이디_없을때() throws UnAuthenticationException {
        final Long notExistId = 9L;
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final Question question = new Question("title", "contents");


        qnaService.update(loginUser, notExistId, question);
    }

    @Test(expected = UnAuthenticationException.class)
    public void update_로그인_안한_사용자() throws UnAuthenticationException {
        final User guest = User.GUEST_USER;
        final Long questionId = 1L;
        final Question question = new Question("title", "contents");


        qnaService.update(guest, questionId, question);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_글_작성자가_다를때() throws UnAuthenticationException {
        final Long questionId = 1L;
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final User anotherUser = new User(2L,"anotherId", "password", "another", "email");
        final Question question = new Question("title", "contents");
        final Question findedQuestion = new Question("title1", "contents1");
        findedQuestion.writeBy(anotherUser);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(findedQuestion));


        qnaService.update(loginUser, questionId, question);
    }

    @Test
    public void deleteQuestion() throws UnAuthenticationException {
        final Long questionId = 1L;
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final Question mockQuestion = mock(Question.class);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(mockQuestion));


        qnaService.deleteQuestion(loginUser, questionId);


        verify(mockQuestion, times(1)).delete(loginUser);
        verify(deleteHistoryService, times(1)).saveAll(anyList());
    }

    @Test(expected = UnAuthenticationException.class)
    public void deleteQuestion_로그인_안함() throws UnAuthenticationException {
        final User guest = User.GUEST_USER;
        final Long questionId = 1L;


        qnaService.deleteQuestion(guest, questionId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void deleteQuestion_질문_없을때() throws UnAuthenticationException {
        final Long questionId = 1L;
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.empty());


        qnaService.deleteQuestion(loginUser, questionId);
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestion_작성자_다름() throws UnAuthenticationException {
        final Long questionId = 1L;
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final User anotherUser = new User(9L, "id", "pwd", "nm", "em");
        final Question findedQuestion = new Question();
        findedQuestion.writeBy(anotherUser);


        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(findedQuestion));


        qnaService.deleteQuestion(loginUser, questionId);
    }
}