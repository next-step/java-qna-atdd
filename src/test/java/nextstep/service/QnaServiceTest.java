package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {

    private User loginUser;
    private Question question;
    private User writer;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Before
    public void setUp() throws Exception {
        this.loginUser = new User("namjunemy", "1234", "njkim", "njkim@slipp.net");
        this.question = new Question("제목입니다", "내용입니다");
        this.writer = new User("nj", "test", "nj", "nj@slipp.net");
    }

    @Test
    public void 질문_조회() throws Exception {
        question.setId(1L);
        question.writeBy(loginUser);
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        Question findQuestion = qnaService.findById(loginUser, question.getId());
        softly.assertThat(findQuestion).isEqualTo(question);
    }

    @Test(expected = EntityNotFoundException.class)
    public void 질문_조회_EntityNotFoundException() {
        question.writeBy(loginUser);
        question.setId(1L);

        when(questionRepository.findById(question.getId())).thenReturn(Optional.empty());

        qnaService.findById(loginUser, question.getId());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문_조회_UnAuthorizedException() {
        loginUser.setId(2L);

        writer.setId(1L);

        Question question = new Question("제목입니다", "내용입니다");
        question.writeBy(writer);
        question.setId(1L);

        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        qnaService.findById(loginUser, question.getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void 질문_업데이트_EntityNotFoundException() {
        loginUser.setId(2L);
        writer.setId(1L);

        question.writeBy(writer);
        question.setId(1L);

        //then
        qnaService.update(loginUser, 2L, question);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문_업데이트_UnAuthorizedException() {
        loginUser.setId(2L);

        writer.setId(1L);

        question.writeBy(writer);
        question.setId(1L);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.update(loginUser, 1L, question);
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문_업데이트_CannotDeleteException() {
        loginUser.setId(2L);

        question.writeBy(loginUser);
        question.setId(1L);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        qnaService.deleteQuestion(loginUser, 1L);

        qnaService.update(loginUser, 1L, new Question("제목 수정", "내용 수정"));
    }

    @Test
    public void 질문_업데이트_성공() {
        loginUser.setId(2L);

        question.writeBy(loginUser);
        question.setId(1L);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.update(loginUser, 1L, new Question("제목 수정", "내용 수정"));
        assertThat(question.getTitle()).isEqualTo("제목 수정");
        assertThat(question.getContents()).isEqualTo("내용 수정");
    }

    @Test(expected = EntityNotFoundException.class)
    public void 질문_삭제_EntityNotFoundException() {
        loginUser.setId(2L);

        writer.setId(1L);

        question.writeBy(writer);
        question.setId(1L);

        //then
        qnaService.deleteQuestion(loginUser, 2L);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문_삭제_UnAuthorizedException() {
        loginUser.setId(2L);

        writer.setId(1L);

        question.writeBy(writer);
        question.setId(1L);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(loginUser, 1L);
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문_삭제_CannotDeleteException() {
        loginUser.setId(2L);

        question.writeBy(loginUser);
        question.setId(1L);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        qnaService.deleteQuestion(loginUser, 1L);

        qnaService.deleteQuestion(loginUser, 1L);
    }

    @Test
    public void 질문_삭제_성공() {
        loginUser.setId(2L);

        question.writeBy(loginUser);
        question.setId(1L);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(loginUser, 1L);
        assertTrue(question.isDeleted());
    }
}
