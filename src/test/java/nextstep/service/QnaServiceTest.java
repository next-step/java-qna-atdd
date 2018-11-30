package nextstep.service;

import nextstep.UnAuthenticationException;
import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;
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
        final Question mockQuestion = mock(Question.class);
        when(questionRepository.save(mockQuestion)).thenReturn(mockQuestion);

        final Question created = qnaService.create(JAVAJIGI, mockQuestion);

        verify(mockQuestion, times(1)).writeBy(JAVAJIGI);
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
        final Question question = new Question("title", "contents");
        final Question mockQuestion = mock(Question.class);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(mockQuestion));
        when(mockQuestion.update(JAVAJIGI, question)).thenReturn(mockQuestion);

        final Question update = qnaService.update(JAVAJIGI, questionId, question);

        verify(mockQuestion, times(1)).update(JAVAJIGI, question);
        softly.assertThat(update).isEqualTo(mockQuestion);
    }

    @Test(expected = EntityNotFoundException.class)
    public void update_질문_아이디_없을때() throws UnAuthenticationException {
        final Long notExistId = 9L;
        final Question question = new Question("title", "contents");
        qnaService.update(JAVAJIGI, notExistId, question);
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
        final Question question = new Question("title", "contents");
        final Question findedQuestion = new Question("title1", "contents1");
        findedQuestion.writeBy(SANJIGI);

        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(findedQuestion));

        qnaService.update(JAVAJIGI, questionId, question);
    }

    @Test
    public void deleteQuestion() throws UnAuthenticationException {
        final Long questionId = 1L;
        final Question mockQuestion = mock(Question.class);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(mockQuestion));

        qnaService.deleteQuestion(JAVAJIGI, questionId);

        verify(mockQuestion, times(1)).delete(JAVAJIGI);
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
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.empty());

        qnaService.deleteQuestion(JAVAJIGI, questionId);
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestion_작성자_다름() throws UnAuthenticationException {
        final Long questionId = 1L;
        final Question findedQuestion = new Question();
        findedQuestion.writeBy(SANJIGI);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(findedQuestion));

        qnaService.deleteQuestion(JAVAJIGI, questionId);
    }

    @Test
    public void addAnswer() throws UnAuthenticationException {
        final Long questionId = 1L;
        final Answer answer = new Answer("answer!!");
        final Question mockQuestion = mock(Question.class);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(mockQuestion));

        final Answer addAnswer = qnaService.addAnswer(JAVAJIGI, questionId, answer);

        verify(mockQuestion, times(1)).addAnswer(any(Answer.class));
        softly.assertThat(addAnswer).isEqualTo(answer);
        softly.assertThat(addAnswer.isOwner(JAVAJIGI)).isTrue();
    }

    @Test(expected = UnAuthenticationException.class)
    public void addAnswer_로그인_안했을때() throws UnAuthenticationException {
        final Long questionId = 1L;
        final User guest = User.GUEST_USER;
        final Answer answer = new Answer("answer!!");

        qnaService.addAnswer(guest, questionId, answer);
    }

    @Test(expected = EntityNotFoundException.class)
    public void addAnswer_질문_없을때() throws UnAuthenticationException {
        final Long questionId = Long.MAX_VALUE;
        final Answer answer = new Answer("answer!!");
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.empty());

        qnaService.addAnswer(JAVAJIGI, questionId, answer);
    }

    @Test
    public void deleteAnswer() throws UnAuthenticationException {
        final Long answerId = 1L;
        final Long questionId = 1L;
        final Answer mockAnswer = mock(Answer.class);
        when(answerRepository.findAnswerByIdAndDeleted(answerId, false)).thenReturn(Optional.of(mockAnswer));

        qnaService.deleteAnswer(JAVAJIGI, questionId, answerId);

        verify(mockAnswer, times(1)).delete(JAVAJIGI, questionId);
        verify(deleteHistoryService, times(1)).saveAll(anyList());
    }

    @Test(expected = UnAuthenticationException.class)
    public void deleteAnswer_로그인_안했을때() throws UnAuthenticationException {
        final User guest = User.GUEST_USER;
        final Long questionId = 1L;
        final Long answerId = 1L;

        qnaService.deleteAnswer(guest, questionId, answerId);
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteAnswer_작성자_다를때() throws UnAuthenticationException {
        final Long questionId = 1L;
        final Long answerId = 1L;
        final Answer findedAnswer = new Answer(JAVAJIGI, "contents!!");
        when(answerRepository.findAnswerByIdAndDeleted(answerId, false)).thenReturn(Optional.of(findedAnswer));

        qnaService.deleteAnswer(SANJIGI, questionId, answerId);
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteAnswer_questionId_다를때() throws UnAuthenticationException {
        final Question question = new Question(1L, "title", "contents");
        final Long answerId = 1L;
        final Answer findedAnswer = new Answer(JAVAJIGI, "contents!!");
        findedAnswer.toQuestion(question);
        when(answerRepository.findAnswerByIdAndDeleted(answerId, false)).thenReturn(Optional.of(findedAnswer));

        qnaService.deleteAnswer(JAVAJIGI, question.getId() + 1, answerId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void deleteAnswer_답변_없을때() throws UnAuthenticationException {
        final Long questionId = 1L;
        final Long answerId = Long.MAX_VALUE;
        when(answerRepository.findAnswerByIdAndDeleted(answerId, false)).thenReturn(Optional.empty());

        qnaService.deleteAnswer(JAVAJIGI, questionId, answerId);
    }

}