package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

/**
 * Created by hspark on 28/11/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    @InjectMocks
    private QnaService qnaService;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private DeleteHistoryService deleteHistoryService;

    @Test
    public void test_질문삭제() throws CannotDeleteException {
        User user = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
        long questionId = 1L;
        Question question = new Question("test", "test1");
        question.setId(questionId);
        question.writeBy(user);

        Mockito.when(questionRepository.findByIdAndDeletedFalse(questionId)).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(user, questionId);

        Assertions.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = EntityNotFoundException.class)
    public void test_질문없음() throws CannotDeleteException {
        User user = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
        long questionId = 1L;

        Mockito.when(questionRepository.findByIdAndDeletedFalse(questionId)).thenReturn(Optional.empty());
        qnaService.deleteQuestion(user, questionId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void test_답변추가_질문없음() throws CannotDeleteException {
        User user = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
        long questionId = 1L;

        Mockito.when(questionRepository.findByIdAndDeletedFalse(questionId)).thenReturn(Optional.empty());

        qnaService.addAnswer(user, questionId, "테스트");
    }

    @Test
    public void test_답변추가() throws CannotDeleteException {
        User user = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
        long questionId = 1L;
        Question question = new Question("test", "test1");
        question.setId(questionId);
        question.writeBy(user);

        Mockito.when(questionRepository.findByIdAndDeletedFalse(questionId)).thenReturn(Optional.of(question));
        Answer actual = qnaService.addAnswer(user, questionId, "테스트");

        Assertions.assertThat(actual.isEqualContents("테스트")).isTrue();
        Assertions.assertThat(actual.isOwner(user)).isTrue();
    }
}