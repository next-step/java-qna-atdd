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

import static nextstep.domain.AnswerTest.*;
import static nextstep.domain.QuestionTest.*;
import static nextstep.domain.UserTest.SELF_USER;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Mock
    QuestionRepository questionRepository;

    @Mock
    AnswerRepository answerRepository;

    @InjectMocks
    QnaService qnaService;

    @Test(expected = CannotUpdateException.class)
    public void update_question_another() throws Exception {
        Question question = anotherQuestion();
        when(questionRepository.findById(ANOTHER_QUESTION_ID)).thenReturn(Optional.of(question));

        qnaService.updateQuestion(SELF_USER, question.getId(), new QuestionDto());
    }

    @Test
    public void update_question_self() throws Exception {
        Question question = selfQuestion();
        when(questionRepository.findById(SELF_QUESTION_ID)).thenReturn(Optional.of(question));

        String title = "updateTitle";
        String contents = "updateContents";

        Question result = qnaService.updateQuestion(
                SELF_USER, question.getId(), new QuestionDto(title, contents));

        softly.assertThat(result.getTitle()).isEqualTo(title);
        softly.assertThat(result.getContents()).isEqualTo(contents);

        softly.assertThat(question.getTitle()).isEqualTo(title);
        softly.assertThat(question.getContents()).isEqualTo(contents);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_question_another() throws Exception {
        Question question = anotherQuestion();
        when(questionRepository.findById(ANOTHER_QUESTION_ID)).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(SELF_USER, question.getId());
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_question_self_contains_another_answers() throws Exception {
        Question question = selfQuestion();
        question.addAnswer(ANOTHER_ANSWER);
        when(questionRepository.findById(ANOTHER_QUESTION_ID)).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(SELF_USER, ANOTHER_QUESTION_ID);
    }

    @Test
    public void delete_question_self_only_self_answers() throws Exception {
        Question question = selfQuestion();
        question.addAnswer(SELF_ANSWER);
        when(questionRepository.findById(SELF_QUESTION_ID)).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(SELF_USER, SELF_QUESTION_ID);

        softly.assertThat(question.isDeleted()).isTrue();
        softly.assertThat(question.getAnswers())
                .allMatch(answer -> answer.isDeleted());
    }

    @Test
    public void create_answer() {
        Question question = selfQuestion();
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        String contents = "answer";
        User loginUser = SELF_USER;
        Answer created = qnaService.createAnswer(loginUser, question.getId(), contents);

        softly.assertThat(created.isOwner(loginUser)).isTrue();
        softly.assertThat(created.isOf(question)).isTrue();
        softly.assertThat(created.getContents()).isEqualTo(contents);
    }

    @Test
    public void find_answers_by_question_id() {
        Question question = selfQuestion();
        question.addAnswer(SELF_ANSWER);
        question.addAnswer(ANOTHER_ANSWER);
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        List<Answer> answers = qnaService.findAnswers(question.getId());

        softly.assertThat(answers)
                .isNotNull()
                .allMatch(answer -> answer.isOf(question))
                .containsAll(Arrays.asList(SELF_ANSWER, ANOTHER_ANSWER));
    }

    @Test
    public void find_answer_by_id() {
        when(answerRepository.findById(SELF_ANSWER_ID)).thenReturn(Optional.of(SELF_ANSWER));

        Answer answer = answerRepository.findById(SELF_ANSWER_ID).get();

        softly.assertThat(answer).isNotNull();
        softly.assertThat(answer.getId()).isEqualTo(SELF_ANSWER_ID);
    }

    @Test(expected = CannotUpdateException.class)
    public void update_answer_another() throws Exception {
        when(answerRepository.findById(ANOTHER_ANSWER_ID)).thenReturn(Optional.of(ANOTHER_ANSWER));

        qnaService.updateAnswer(SELF_USER, ANOTHER_ANSWER_ID, "updateQuestion");
    }

    @Test
    public void update_answer_self() throws Exception {
        when(answerRepository.findById(SELF_ANSWER_ID)).thenReturn(Optional.of(SELF_ANSWER));

        String contents = "updateQuestion";
        Answer answer = qnaService.updateAnswer(SELF_USER, SELF_ANSWER_ID, contents);

        softly.assertThat(answer.getContents()).isEqualTo(contents);
        softly.assertThat(SELF_ANSWER.getContents()).isEqualTo(contents);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_answer_another() throws Exception {
        when(answerRepository.findById(ANOTHER_QUESTION_ID)).thenReturn(Optional.of(ANOTHER_ANSWER));

        qnaService.deleteAnswer(SELF_USER, ANOTHER_QUESTION_ID);
    }

    @Test
    public void delete_answer_self() throws Exception {
        when(answerRepository.findById(SELF_ANSWER_ID)).thenReturn(Optional.of(SELF_ANSWER));

        qnaService.deleteAnswer(SELF_USER, SELF_ANSWER_ID);

        softly.assertThat(SELF_ANSWER.isDeleted()).isTrue();
    }
}