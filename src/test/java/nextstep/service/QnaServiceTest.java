package nextstep.service;

import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import nextstep.CannotDeleteException;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.domain.UserTest;
import support.test.BaseTest;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    private User loginUser = UserTest.SANJIGI;

    @Test
    public void create() {
        Question question = new Question("타이틀입니다.", "내용입니다.");
        when(questionRepository.save(question)).thenReturn(question);
        
        Question savedQuestion = qnaService.create(loginUser, question);
        softly.assertThat(question).isEqualTo(savedQuestion);
    }
    
    @Test
    public void delete_success() throws CannotDeleteException {
        Question question = new Question("타이틀입니다.", "내용입니다.", loginUser);
        long id = 1l;
        when(questionRepository.findById(id)).thenReturn(Optional.of(question));
        
        qnaService.deleteQuestion(loginUser, id);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_failed_when_not_owner() throws CannotDeleteException {
        User ownerUser = new User(1l, "javajigi", "password", "name", "javajigi@slipp.net");
        Question question = new Question("타이틀입니다.", "내용입니다.", ownerUser);
        long id = 1l;
        when(questionRepository.findById(id)).thenReturn(Optional.of(question));
        
        qnaService.deleteQuestion(loginUser, id);
    }
}