package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    @Before
    public void setUp() {
        when(this.questionRepository.findById(1L)).thenReturn(Optional.of(QuestionTest.ORIGINAL_QUESTION));
        when(this.answerRepository.findById(1L)).thenReturn(Optional.of(AnswerTest.ORIGIN_ANSWER));
    }

    @Before
    public void initQnaService() {
        QuestionTest.ORIGINAL_QUESTION.writeBy(UserTest.JAVAJIGI);

    }

    @Test
    public void update_owner() {
        this.qnaService.update(UserTest.JAVAJIGI,1L, QuestionTest.UPDATE_QUESTION);
        softly.assertThat(QuestionTest.ORIGINAL_QUESTION.getTitle()).isEqualTo(QuestionTest.UPDATE_QUESTION.getTitle());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws UnAuthorizedException{
        this.qnaService.update(UserTest.SANJIGI,1L, QuestionTest.UPDATE_QUESTION);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_guest() throws UnAuthorizedException{
        this.qnaService.update(User.GUEST_USER,1L, QuestionTest.UPDATE_QUESTION);
    }

    @Test
    public void delete_owner() throws CannotDeleteException {
        this.qnaService.deleteQuestion(UserTest.JAVAJIGI, 1L);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws CannotDeleteException {
        this.qnaService.deleteQuestion(UserTest.SANJIGI, 1L);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_guest() throws CannotDeleteException {
        this.qnaService.deleteQuestion(User.GUEST_USER, 1L);
    }

    @Test
    public void create() {
        this.qnaService.addAnswer(UserTest.JAVAJIGI, 1L, AnswerTest.ORIGIN_ANSWER);
    }

    @Test(expected = EntityNotFoundException.class)
    public void create_not_exist_question() {
        this.qnaService.addAnswer(UserTest.JAVAJIGI, 10L, AnswerTest.ORIGIN_ANSWER);
    }


    @Test
    public void detail_answer() throws CannotDeleteException {
        Answer answer = this.qnaService.deleteAnswer(UserTest.JAVAJIGI, 1L);
        assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_answer_not_owner() throws CannotDeleteException {
        this.qnaService.deleteAnswer(UserTest.SANJIGI, 1L);
    }
}
