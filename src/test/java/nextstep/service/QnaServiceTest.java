package nextstep.service;

import nextstep.UnAuthenticationException;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
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

    private static final User LOGIN_USER = new User(1L, "testid", "test", "이름", "test@test.com");
    private static final User OTHER_USER = new User(2L, "otherid", "other", "다른사람", "other@other.com");

    private static final String ORIGINAL_CONTENTS = "내용 원본";
    private static final String ORIGINAL_TITLE = "제목 원본";

    private static final String CHANGED_TITLE = "제목 수정";
    private static final String CHANGED_CONTENTS = "내용 수정";

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    @InjectMocks
    private QnaService qnaService;


    @Test
    public void 로그인사용자_본인_글_업데이트_가능() throws UnAuthenticationException {
        User user = LOGIN_USER;
        Question question = new Question(ORIGINAL_TITLE, ORIGINAL_CONTENTS);
        question.writeBy(user);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        qnaService.update(user,1L, new Question(CHANGED_TITLE, CHANGED_CONTENTS));

        softly.assertThat(question.getTitle()).isEqualTo(CHANGED_TITLE);
    }


    @Test(expected = UnAuthenticationException.class)
    public void 로그인사용자_다른사람_글_업데이트_불가능() throws UnAuthenticationException {
        User user = LOGIN_USER;
        Question question = new Question(ORIGINAL_TITLE, ORIGINAL_CONTENTS);
        question.writeBy(user);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        User otherUser = OTHER_USER;
        qnaService.update(otherUser, 1L, new Question(CHANGED_TITLE, CHANGED_CONTENTS));

        softly.assertThat(question.getTitle()).isEqualTo(ORIGINAL_TITLE);
    }


    @Test(expected = UnAuthenticationException.class)
    public void 로그인_안한_사용자_다른사람_글_업데이트_불가능() throws UnAuthenticationException {
        User user = LOGIN_USER;
        Question question = new Question(ORIGINAL_TITLE, ORIGINAL_CONTENTS);
        question.writeBy(user);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        User notLoginUser = User.GUEST_USER;
        qnaService.update(notLoginUser, 1L, new Question(CHANGED_TITLE, CHANGED_CONTENTS));

        softly.assertThat(questionRepository.findById(question.getId()).get().getTitle()).isEqualTo(ORIGINAL_TITLE);
    }

    @Test
    public void 로그인_사용자_본인_글_삭제_가능() throws UnAuthenticationException {
        User user = LOGIN_USER;
        Question question = new Question(ORIGINAL_TITLE, ORIGINAL_CONTENTS);
        question.writeBy(user);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        qnaService.deleteQuestion(user, 1L);

        softly.assertThat(questionRepository.findById(question.getId())).isEmpty();
    }

    @Test(expected = UnAuthenticationException.class)
    public void 로그인_사용자_다른사람_글_삭제_불가능() throws UnAuthenticationException {
        User user = LOGIN_USER;
        Question question = new Question(ORIGINAL_TITLE, ORIGINAL_CONTENTS);
        question.writeBy(user);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        User otherUser = OTHER_USER;
        qnaService.deleteQuestion(otherUser, 1L);

        softly.assertThat(questionRepository.findById(question.getId())).isNotEmpty();
    }

    @Test(expected = UnAuthenticationException.class)
    public void 로그인_안한_사용자_다른사람_글_삭제_불가능() throws UnAuthenticationException {
        User user = LOGIN_USER;
        Question question = new Question(ORIGINAL_TITLE, ORIGINAL_CONTENTS);
        question.writeBy(user);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        User notLoginUser = User.GUEST_USER;
        qnaService.deleteQuestion(notLoginUser, 1L);

        softly.assertThat(questionRepository.findById(question.getId())).isNotEmpty();
    }




}