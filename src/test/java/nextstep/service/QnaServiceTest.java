package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.domain.UserTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    private static final Question ORIGINAL_QUESTION = new Question("title", "contents");
    private static final Question UPDATE_QUESTION = new Question("update title", "update contents");

    @Before
    public void initQnaService() {
        ORIGINAL_QUESTION.writeBy(UserTest.JAVAJIGI);
    }

    @Test
    public void update_owner() {
        when(this.questionRepository.findById(1L)).thenReturn(Optional.of(ORIGINAL_QUESTION));
        this.qnaService.update(UserTest.JAVAJIGI,1L, UPDATE_QUESTION);
        softly.assertThat(ORIGINAL_QUESTION.getTitle()).isEqualTo(UPDATE_QUESTION.getTitle());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws UnAuthorizedException{
        when(this.questionRepository.findById(1L)).thenReturn(Optional.of(ORIGINAL_QUESTION));
        this.qnaService.update(UserTest.SANJIGI,1L, UPDATE_QUESTION);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_quest() throws UnAuthorizedException{
        when(this.questionRepository.findById(1L)).thenReturn(Optional.of(ORIGINAL_QUESTION));
        this.qnaService.update(User.GUEST_USER,1L, UPDATE_QUESTION);
    }

    @Test
    public void delete_owner() throws CannotDeleteException {
        when(this.questionRepository.findById(1L)).thenReturn(Optional.of(ORIGINAL_QUESTION));
        this.qnaService.deleteQuestion(UserTest.JAVAJIGI, 1L);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws CannotDeleteException {
        when(this.questionRepository.findById(1L)).thenReturn(Optional.of(ORIGINAL_QUESTION));
        this.qnaService.deleteQuestion(UserTest.SANJIGI, 1L);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_guest() throws CannotDeleteException {
        when(this.questionRepository.findById(1L)).thenReturn(Optional.of(ORIGINAL_QUESTION));
        this.qnaService.deleteQuestion(User.GUEST_USER, 1L);
    }
}
