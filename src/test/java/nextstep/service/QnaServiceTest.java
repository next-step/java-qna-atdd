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
        when(questionRepository.findById(ANOTHER_QUESTION_ID)).thenReturn(Optional.of(ANOTHER_QUESTION));

        qnaService.updateQuestion(SELF_USER, ANOTHER_QUESTION_ID, new QuestionDto());
    }

    @Test
    public void update_question_self() throws Exception {
        when(questionRepository.findById(SELF_QUESTION_ID)).thenReturn(Optional.of(SELF_QUESTION));

        String title = "updateTitle";
        String contents = "updateContents";

        Question updatedQuestion = SELF_QUESTION;
        updatedQuestion.setTitle(title);
        updatedQuestion.setContents(contents);

        Question result = qnaService.updateQuestion(
                SELF_USER, updatedQuestion.getId(), new QuestionDto(title, contents));

        softly.assertThat(result.getTitle()).isEqualTo(title);
        softly.assertThat(result.getContents()).isEqualTo(contents);

        softly.assertThat(SELF_QUESTION.getTitle()).isEqualTo(title);
        softly.assertThat(SELF_QUESTION.getContents()).isEqualTo(contents);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_question_another() throws Exception {
        when(questionRepository.findById(ANOTHER_QUESTION_ID)).thenReturn(Optional.of(ANOTHER_QUESTION));

        qnaService.deleteQuestion(SELF_USER, ANOTHER_QUESTION_ID);
    }

    @Test
    public void delete_question_self() throws Exception {
        when(questionRepository.findById(SELF_QUESTION_ID)).thenReturn(Optional.of(SELF_QUESTION));

        qnaService.deleteQuestion(SELF_USER, SELF_QUESTION_ID);

        softly.assertThat(SELF_QUESTION.isDeleted()).isTrue();
    }

    @Test
    public void create_answer() {
        when(questionRepository.findById(SELF_QUESTION_ID)).thenReturn(Optional.of(SELF_QUESTION));

        User loginUser = SELF_USER;
        Question question = SELF_QUESTION;
        Answer created = qnaService.createAnswer(loginUser, question.getId(), "answer");

        softly.assertThat(created.isOwner(loginUser)).isTrue();
        softly.assertThat(created.isOf(question)).isTrue();
    }

    @Test
    public void find_answers_by_question_id() {
        when(questionRepository.findById(SELF_QUESTION_ID)).thenReturn(Optional.of(SELF_QUESTION));

        Question question = SELF_QUESTION;
        List<Answer> answers = qnaService.findAnswers(SELF_QUESTION_ID);

        softly.assertThat(answers).isNotNull();
        softly.assertThat(answers)
                .allMatch(answer -> answer.isOf(question))
                .containsAll(Arrays.asList(SELF_ANSWER_OF_DEFAULT_QUESTION, ANOTHER_ANSWER_OF_DEFAULT_QUESTION));
    }

    @Test
    public void find_answer_by_id() {
        when(answerRepository.findById(SELF_ANSWER_ID)).thenReturn(Optional.of(SELF_ANSWER_OF_DEFAULT_QUESTION));

        Answer answer = answerRepository.findById(SELF_ANSWER_ID).get();

        softly.assertThat(answer).isNotNull();
        softly.assertThat(answer.getId()).isEqualTo(SELF_ANSWER_ID);
    }

    @Test(expected = CannotUpdateException.class)
    public void update_answer_another() throws Exception {
        when(answerRepository.findById(ANOTHER_ANSWER_ID)).thenReturn(Optional.of(ANOTHER_ANSWER_OF_DEFAULT_QUESTION));

        qnaService.updateAnswer(SELF_USER, ANOTHER_ANSWER_ID, "updateQuestion");
    }

    @Test
    public void update_answer_self() throws Exception {
        when(answerRepository.findById(SELF_ANSWER_ID)).thenReturn(Optional.of(SELF_ANSWER_OF_DEFAULT_QUESTION));

        String contents = "updateQuestion";
        Answer answer = qnaService.updateAnswer(SELF_USER, SELF_ANSWER_ID, contents);

        softly.assertThat(answer.getContents()).isEqualTo(contents);
        softly.assertThat(SELF_ANSWER_OF_DEFAULT_QUESTION.getContents()).isEqualTo(contents);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_answer_another() throws Exception {
        when(answerRepository.findById(ANOTHER_QUESTION_ID)).thenReturn(Optional.of(ANOTHER_ANSWER_OF_DEFAULT_QUESTION));

        qnaService.deleteAnswer(SELF_USER, ANOTHER_QUESTION_ID);
    }

    @Test
    public void delete_answer_self() throws Exception {
        when(answerRepository.findById(SELF_ANSWER_ID)).thenReturn(Optional.of(SELF_ANSWER_OF_DEFAULT_QUESTION));

        qnaService.deleteAnswer(SELF_USER, SELF_ANSWER_ID);

        softly.assertThat(SELF_ANSWER_OF_DEFAULT_QUESTION.isDeleted()).isTrue();
    }
}