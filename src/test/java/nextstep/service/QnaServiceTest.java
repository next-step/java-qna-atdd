package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.UnAuthenticationException;
import nextstep.UnAuthorizedException;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.domain.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import support.test.BaseTest;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class QnaServiceTest extends BaseTest {

    private User loginUser;
    private User otherLoginUser;

    @Autowired
    private QnaService qnaService;

    @Before
    public void setUp() throws Exception {
        loginUser = new User("javajigi", "test", "자바지기", "javajigi@slipp.net");
        loginUser.setId(1L);
        otherLoginUser = new User("sanjigi", "test", "산지기", "sanjigi@slipp.net");
        otherLoginUser.setId(2L);
    }

    @Test
    public void 질문_조회() throws Exception {
        Question findQuestion = qnaService.findById(loginUser, 1L);
        softly.assertThat(findQuestion).isNotNull();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 다른_사용자_질문_조회() {
        qnaService.findById(loginUser, 2L);
    }

    @Test(expected = EntityNotFoundException.class)
    public void 질문_업데이트_없는경우() {
        Question question = new Question("제목입니다", "내용입니다");
        qnaService.update(loginUser, 100L, question);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 다른_사용자_질문_업데이트() {
        qnaService.update(loginUser, 2L, new Question("제목 수정", "내용 수정"));
    }

    @Test
    public void 질문_업데이트_성공() {
        qnaService.update(loginUser, 1L, new Question("제목 수정", "내용 수정"));
        softly.assertThat(qnaService.findById(1L).orElseThrow(EntityNotFoundException::new).getTitle()).isEqualTo("제목 수정");
        softly.assertThat(qnaService.findById(1L).orElseThrow(EntityNotFoundException::new).getContents()).isEqualTo("내용 수정");
    }

    @Test(expected = EntityNotFoundException.class)
    public void 삭제할_질문_없는경우() throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, 100L);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 다른_사용자_질문_삭제() throws CannotDeleteException {
        qnaService.deleteQuestion(otherLoginUser, 1L);
    }

    @Test
    public void 질문_삭제_성공() throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, 1L);
        softly.assertThat(qnaService.findById(1L).orElseThrow(EntityNotFoundException::new).isDeleted()).isTrue();
    }
}


