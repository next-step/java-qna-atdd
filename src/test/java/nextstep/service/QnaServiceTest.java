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
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    private static final String TITLE = "테스트 타이틀";
    private static final String CONTENTS = "테스트 컨텐츠";
    private User user;
    private Question question;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Before
    public void setup() {
        user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        question = new Question(TITLE, CONTENTS);
    }

    @Test
    public void findByIdWithAuthorized() {
        qnaService.create(user, question);

        when(questionRepository.findById(0L)).thenReturn(Optional.of(question));

        Question findQuestion = qnaService.findByIdWithAuthorized(user, 0);

        softly.assertThat(findQuestion.getTitle()).isEqualTo(TITLE);
        softly.assertThat(findQuestion.getContents()).isEqualTo(CONTENTS);
    }

    @Test(expected = UnAuthorizedException.class)
    public void findByIdWithAuthorizedByAnotherUser() {
        User anotherUser = new User("mirrors89", "password", "name", "mirrors89@slipp.net");

        qnaService.create(user, question);

        when(questionRepository.findById(0L)).thenReturn(Optional.of(question));

        qnaService.findByIdWithAuthorized(anotherUser, 0);
    }

}
