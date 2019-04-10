package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.domain.*;
import nextstep.dto.QuestionDto;
import nextstep.web.QuestionAcceptanceTest;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.test.BaseTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Mock
    UserRepository userRepository;

    @Mock
    QuestionRepository questionRepository;

    @Mock
    AnswerRepository answerRepository;

    @InjectMocks
    QnaService qnaService;

    @Before
    public void setup() {
        User defaultUser = new User(DEFAULT_LOGIN_USER, "password", DEFAULT_LOGIN_USER, "javajigi@slipp.net");
        User anotherUser = new User(ANOTHER_LOGIN_USER, "password", ANOTHER_LOGIN_USER, "javajigi@slipp.net");
        when(userRepository.findByUserId(DEFAULT_LOGIN_USER)).thenReturn(Optional.of(defaultUser));

        Question defaultQuestion = new Question("defaultTitle", "defaultContent");
        defaultQuestion.setId(DEFAULT_ANSWER_ID);
        defaultQuestion.writeBy(defaultUser);
        when(questionRepository.findById(DEFAULT_QUESTION_ID)).thenReturn(Optional.of(defaultQuestion));

        Question anotherQuestion = new Question("anotherTitle", "anotherContent");
        anotherQuestion.setId(ANOTHER_QUESTION_ID);
        anotherQuestion.writeBy(anotherUser);
        when(questionRepository.findById(ANOTHER_QUESTION_ID)).thenReturn(Optional.of(anotherQuestion));

        Answer defaultAnswer = new Answer(defaultUser, "selfAnswer");
        defaultAnswer.toQuestion(defaultQuestion);
        defaultAnswer.setId(DEFAULT_ANSWER_ID);
        Answer anotherAnswer = new Answer(anotherUser, "anotherAnswer");
        anotherAnswer.toQuestion(defaultQuestion);
        anotherAnswer.setId(ANOTHER_ANSWER_ID);

        when(answerRepository.findAllByQuestion(defaultQuestion))
                .thenReturn(Arrays.asList(defaultAnswer, anotherAnswer));
        when(answerRepository.findById(DEFAULT_ANSWER_ID))
                .thenReturn(Optional.of(defaultAnswer));
        when(answerRepository.findById(ANOTHER_ANSWER_ID))
                .thenReturn(Optional.of(anotherAnswer));
    }

    @Test(expected = CannotUpdateException.class)
    public void update_question_another() throws Exception {
        qnaService.updateQuestion(defaultUser(), ANOTHER_QUESTION_ID, new QuestionDto());
    }

    @Test
    public void update_question_self() throws Exception {
        String title = "updateTitle";
        String contents = "updateContents";

        Question updatedQuestion = defaultQuestion();
        updatedQuestion.setTitle(title);
        updatedQuestion.setContents(contents);

        Question result = qnaService.updateQuestion(defaultUser(), DEFAULT_QUESTION_ID, new QuestionDto(title, contents));

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

    @Test
    public void create_answer() {
        User loginUser = defaultUser();
        Question question = defaultQuestion();
        Answer created = qnaService.createAnswer(loginUser, question.getId(), "answer");

        softly.assertThat(created.isOwner(loginUser)).isTrue();
        softly.assertThat(created.isOf(question)).isTrue();
    }

    @Test
    public void find_answers_by_question_id() {
        Question question = defaultQuestion();
        log.debug("question : {}", question);
        List<Answer> answersByQuestionId = qnaService.findAnswers(question.getId());
        softly.assertThat(answersByQuestionId).isNotNull();
        softly.assertThat(answersByQuestionId)
                .allMatch(answer -> answer.isOf(question));
    }

    @Test
    public void find_answer_by_id() {
        Answer answer = defaultAnswer();
        softly.assertThat(answer).isNotNull();
        softly.assertThat(answer.getId()).isEqualTo(DEFAULT_ANSWER_ID);
    }

    @Test(expected = CannotUpdateException.class)
    public void update_answer_another() throws Exception {
        qnaService.updateAnswer(defaultUser(), ANOTHER_ANSWER_ID, "updateQuestion");
    }

    @Test
    public void update_answer_self() throws Exception {
        String contents = "updateQuestion";
        Answer answer = qnaService.updateAnswer(defaultUser(), DEFAULT_ANSWER_ID, contents);
        softly.assertThat(answer.getContents()).isEqualTo(contents);
        softly.assertThat(defaultAnswer().getContents()).isEqualTo(contents);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_answer_another() throws Exception {
        qnaService.deleteAnswer(defaultUser(), ANOTHER_QUESTION_ID);
    }

    @Test
    public void delete_answer_self() throws Exception {
        qnaService.deleteAnswer(defaultUser(), DEFAULT_ANSWER_ID);
        softly.assertThat(defaultAnswer().isDeleted()).isTrue();
    }

    private User defaultUser() {
        return userRepository.findByUserId(DEFAULT_LOGIN_USER).get();
    }

    private Question defaultQuestion() {
        return questionRepository.findById(DEFAULT_QUESTION_ID).get();
    }

    private Answer defaultAnswer() {
        return qnaService.findAnswer(DEFAULT_ANSWER_ID);
    }
}