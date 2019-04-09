package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.domain.UserRepository;
import nextstep.web.QuestionAcceptanceTest;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.test.BaseTest;

import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    protected static final String DEFAULT_LOGIN_USER = "javajigi";
    protected static final String ANOTHER_LOGIN_USER = "another";

    private static final long DEFAULT_QUESTION_ID = 1;
    private static final long ANOTHER_QUESTION_ID = 2;

    @Mock
    UserRepository userRepository;

    @Mock
    QuestionRepository questionRepository;

    @InjectMocks
    QnaService qnaService;

    @Before
    public void setup() {
        User defaultUser = new User(1, DEFAULT_LOGIN_USER, "password", DEFAULT_LOGIN_USER, "javajigi@slipp.net");
        when(userRepository.findByUserId(DEFAULT_LOGIN_USER)).thenReturn(Optional.of(defaultUser));

        Question defaultQuestion = new Question("defaultTitle", "defaultContent");
        defaultQuestion.writeBy(defaultUser);
        when(questionRepository.findById(DEFAULT_QUESTION_ID)).thenReturn(Optional.of(defaultQuestion));

        Question anotherQuestion = new Question("anotherTitle", "anotherContent");
        anotherQuestion.writeBy(new User(1, ANOTHER_LOGIN_USER, "password", ANOTHER_LOGIN_USER, "javajigi@slipp.net"));
        when(questionRepository.findById(ANOTHER_QUESTION_ID)).thenReturn(Optional.of(anotherQuestion));
    }

    @Test(expected = CannotUpdateException.class)
    public void update_question_another() throws Exception {
        qnaService.update(defaultUser(), ANOTHER_QUESTION_ID, defaultQuestion());
    }

    @Test
    public void update_question_self() throws Exception {
        String title = "updateTitle";
        String contents = "updateContents";

        Question updatedQuestion = defaultQuestion();
        updatedQuestion.setTitle(title);
        updatedQuestion.setContents(contents);

        Question result = qnaService.update(defaultUser(), DEFAULT_QUESTION_ID, updatedQuestion);

        softly.assertThat(result.getTitle()).isEqualTo(title);
        softly.assertThat(result.getContents()).isEqualTo(contents);

        softly.assertThat(defaultQuestion().getTitle()).isEqualTo(title);
        softly.assertThat(defaultQuestion().getContents()).isEqualTo(contents);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_question_another() throws Exception {
        qnaService.deleteQuestion(defaultUser(), ANOTHER_QUESTION_ID);
    }

    @Test
    public void delete_question_self() throws Exception {
        qnaService.deleteQuestion(defaultUser(), DEFAULT_QUESTION_ID);
        softly.assertThat(defaultQuestion().isDeleted()).isTrue();
    }

    private User defaultUser() {
        return userRepository.findByUserId(DEFAULT_LOGIN_USER).get();
    }

    private Question defaultQuestion() {
        return questionRepository.findById(DEFAULT_QUESTION_ID).get();
    }
}