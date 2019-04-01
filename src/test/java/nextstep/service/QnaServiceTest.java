package nextstep.service;

import nextstep.UnAuthorizedException;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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
    public void 아이디로_작성된_질문_찾기() throws Exception {

        User loginUser = new User("namjunemy", "1234", "njkim", "njkim@slipp.net");
        Question question = new Question("제목입니다", "내용입니다");
        question.setId(1L);
        question.writeBy(loginUser);
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        Question findQuestion = qnaService.findById(loginUser, question.getId());
        softly.assertThat(findQuestion).isEqualTo(question);
    }

    @Test(expected = EntityNotFoundException.class)
    public void 아이디로_작성된_질문_없음() {
        User loginUser = new User("namjunemy", "1234", "njkim", "njkim@slipp.net");
        Question question = new Question("제목입니다", "내용입니다");
        question.writeBy(loginUser);
        question.setId(1L);

        when(questionRepository.findById(question.getId())).thenReturn(Optional.empty());

        qnaService.findById(loginUser, question.getId());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 다른_아이디의_질문_조회_권한_없음() {
        User loginUser = new User("namjunemy", "1234", "njkim", "njkim@slipp.net");
        loginUser.setId(2L);

        User writer = new User("nj", "test", "nj", "nj@slipp.net");
        writer.setId(1L);

        Question question = new Question("제목입니다", "내용입니다");
        question.writeBy(writer);
        question.setId(1L);

        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        qnaService.findById(loginUser, question.getId());
    }
}
