package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.UnAuthenticationException;
import nextstep.UnAuthorizedException;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.domain.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import support.test.BaseTest;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void 질문_조회() throws Exception {
        User loginUser = new User("sy", "test", "seoyeong", "seoyeong@slipp.net");
        Question question = new Question("제목입니다.", "내용입니다.");
        question.writeBy(loginUser);
        question.setId(1L);
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        Question findQuestion = qnaService.findById(loginUser, question.getId());
        softly.assertThat(findQuestion).isEqualTo(question);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 다른_사용자_질문_조회() {
        User loginUser = new User("seoyeong", "test", "seoyeong", "seoyeong@slipp.net");
        loginUser.setId(2L);
        User writer = new User("sysy", "test", "sysy", "sysy@slipp.net");
        writer.setId(1L);
        Question question = new Question("제목입니다.", "내용입니다.");
        question.writeBy(writer);
        question.setId(1L);
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        qnaService.findById(loginUser, question.getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void 질문_업데이트_없는경우() {
        User loginUser = new User("seoyeong", "test", "seoyeong", "seoyeong@slipp.net");
        loginUser.setId(2L);

        User writer = new User("sysy", "test", "sysy", "sysy@slipp.net");
        writer.setId(1L);

        Question question = new Question("제목입니다", "내용입니다");
        question.writeBy(writer);
        question.setId(1L);


        qnaService.update(loginUser, 2L, question);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 다른_사용자_질문_업데이트() {
        User loginUser = new User("seoyeong", "test", "seoyeong", "seoyeong@slipp.net");
        loginUser.setId(2L);

        User writer = new User("sysy", "test", "sysy", "sysy@slipp.net");
        writer.setId(1L);

        Question question = new Question("제목입니다", "내용입니다");
        question.writeBy(writer);
        question.setId(1L);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.update(loginUser, 1L, question);
    }

    @Test
    public void 질문_업데이트_성공() {
        User loginUser = new User("seoyeong", "test", "seoyeong", "seoyeong@slipp.net");
        loginUser.setId(2L);

        Question question = new Question("제목입니다", "내용입니다");
        question.writeBy(loginUser);
        question.setId(1L);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.update(loginUser, 1L, new Question("제목 수정", "내용 수정"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void 삭제할_질문_없는경우() throws CannotDeleteException {
        User loginUser = new User("seoyeong", "test", "seoyeong", "seoyeong@slipp.net");
        loginUser.setId(2L);

        User writer = new User("sysy", "test", "sysy", "sysy@slipp.net");
        writer.setId(1L);

        Question question = new Question("제목입니다", "내용입니다");
        question.writeBy(writer);
        question.setId(1L);


        qnaService.deleteQuestion(loginUser, 2L);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 다른_사용자_질문_삭제() throws CannotDeleteException {
        User loginUser = new User("seoyeong", "test", "seoyeong", "seoyeong@slipp.net");
        loginUser.setId(2L);

        User writer = new User("sysy", "test", "sysy", "sysy@slipp.net");
        writer.setId(1L);

        Question question = new Question("제목입니다", "내용입니다");
        question.writeBy(writer);
        question.setId(1L);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(loginUser, 1L);
    }

    @Test
    public void 질문_삭제_성공() throws CannotDeleteException {
        User loginUser = new User("seoyeong", "test", "seoyeong", "seoyeong@slipp.net");
        loginUser.setId(2L);

        Question question = new Question("제목입니다", "내용입니다");
        question.writeBy(loginUser);
        question.setId(1L);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(loginUser, 1L);
    }
}


