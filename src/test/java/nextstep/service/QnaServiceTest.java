package nextstep.service;

import nextstep.CannotDeleteException;
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

    private User loginUser;
    private Question question;
    private User guest;
    private final Long questionId = 1L;

    @Before
    public void setup() {
        this.qnaService = new QnaService(questionRepository, answerRepository, deleteHistoryService);
        this.loginUser = new User(1L,"userId", "pass", "name", "email");
        this.question = new Question("title", "contents");
        this.guest = User.GUEST_USER;
    }

    @Test
    public void findById() {
        final Optional<Question> expected = Optional.of(question);

        when(questionRepository.findById(questionId)).thenReturn(expected);

        final Optional<Question> question = qnaService.findById(questionId);

        softly.assertThat(question).isEqualTo(expected);
    }

    @Test
    public void findNotDeletedQuestionById() {
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(question));

        final Question findedQuestion = qnaService.findNotDeletedQuestionById(questionId);

        softly.assertThat(findedQuestion).isEqualTo(question);
    }

    @Test
    public void create() throws UnAuthenticationException {

        final Question mockQuestion = mock(Question.class);
        when(questionRepository.save(mockQuestion)).thenReturn(mockQuestion);

        final Question created = qnaService.create(loginUser, mockQuestion);

        verify(mockQuestion, times(1)).writeBy(loginUser);
        softly.assertThat(created).isEqualTo(mockQuestion);
    }

    @Test(expected = UnAuthenticationException.class)
    public void create_로그인_안한_사용자() throws UnAuthenticationException {
        qnaService.create(guest, question);
    }

    @Test
    public void update() throws UnAuthenticationException {
        final Question mockQuestion = mock(Question.class);
        when(mockQuestion.setTitle(question.getTitle())).thenReturn(mockQuestion);
        when(mockQuestion.setContents(question.getContents())).thenReturn(mockQuestion);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(mockQuestion));
        when(mockQuestion.isOwner(loginUser)).thenReturn(true);

        final Question update = qnaService.update(loginUser, questionId, question);

        verify(mockQuestion, times(1)).setTitle(question.getTitle());
        verify(mockQuestion, times(1)).setContents(question.getContents());
        softly.assertThat(update).isEqualTo(mockQuestion);
    }

    @Test(expected = EntityNotFoundException.class)
    public void update_질문_아이디_없을때() throws UnAuthenticationException {
        final Long notExistId = 9L;

        qnaService.update(loginUser, notExistId, question);
    }

    @Test(expected = UnAuthenticationException.class)
    public void update_로그인_안한_사용자() throws UnAuthenticationException {
        qnaService.update(guest, questionId, question);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_글_작성자가_다를때() throws UnAuthenticationException {
        final User anotherUser = new User(2L,"anotherId", "password", "another", "email");
        final Question findedQuestion = new Question("title1", "contents1");
        findedQuestion.writeBy(anotherUser);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(findedQuestion));

        qnaService.update(loginUser, questionId, question);
    }

    @Test
    public void deleteQuestion() throws UnAuthenticationException {
        final Question mockQuestion = mock(Question.class);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(mockQuestion));
        when(mockQuestion.isOwner(loginUser)).thenReturn(true);

        qnaService.deleteQuestion(loginUser, questionId);

        verify(mockQuestion, times(1)).delete();
        verify(deleteHistoryService, times(1)).saveAll(anyList());
    }

    @Test(expected = UnAuthenticationException.class)
    public void deleteQuestion_로그인_안함() throws UnAuthenticationException {
        qnaService.deleteQuestion(guest, questionId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void deleteQuestion_질문_없을때() throws UnAuthenticationException {
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.empty());

        qnaService.deleteQuestion(loginUser, questionId);
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteQuestion_작성자_다름() throws UnAuthenticationException {
        final User anotherUser = new User(9L, "id", "pwd", "nm", "em");
        final Question findedQuestion = new Question();
        findedQuestion.writeBy(anotherUser);

        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(findedQuestion));

        qnaService.deleteQuestion(loginUser, questionId);
    }
}