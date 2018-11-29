package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Optional;

import static nextstep.domain.QuestionTest.newQuestion;
import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    @Mock
    private DeletePolicy deletePolicy;

    @InjectMocks
    private QnaService qnaService;

    private long questionId = 1l;
    private long answerId = 1l;

    @Test
    public void create() {
        Question question = newQuestion(SANJIGI);

        when(questionRepository.save(question)).thenReturn(question);

        Question savedQuestion = qnaService.create(SANJIGI, question);

        softly.assertThat(question).isEqualTo(savedQuestion);
    }
    
    @Test
    public void delete_success() throws CannotDeleteException {
        Question question = newQuestion(SANJIGI);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(deletePolicy.canPermission(any(), any())).thenReturn(true);
        qnaService.deleteQuestion(SANJIGI, questionId);
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_failed_when_not_owner() throws CannotDeleteException {
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(newQuestion(JAVAJIGI)));
        when(deletePolicy.canPermission(any(), any())).thenReturn(false);
        qnaService.deleteQuestion(SANJIGI, questionId);
    }

    @Test
    public void add_answer() {
        Answer answer = new Answer(SANJIGI, "답변입니다.");
        Question question = newQuestion();

        when(answerRepository.save(answer)).thenReturn(answer);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        qnaService.addAnswer(SANJIGI, questionId, "답변입니다.");

        verify(answerRepository, times(1)).save(answer);
        verify(questionRepository, times(1)).findById(questionId);
    }

    @Test
    public void delete_answer_success() throws CannotDeleteException {
        Answer answer = new Answer(SANJIGI, "답변입니다.");

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));

        Answer deletedAnswer = qnaService.deleteAnswer(SANJIGI, answerId);

        verify(answerRepository, times(1)).findById(answerId);
        softly.assertThat(deletedAnswer.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_answer_failed_when_not_owner() throws CannotDeleteException {
        Answer answer = new Answer(SANJIGI, "답변입니다.");

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));

        Answer deletedAnswer = qnaService.deleteAnswer(JAVAJIGI, answerId);

        verify(answerRepository, times(1)).findById(answerId);
    }
}