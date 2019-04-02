package nextstep.service;

import static nextstep.domain.UserTest.newUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import nextstep.domain.Answer;
import nextstep.domain.AnswerRepository;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    private User writer;
    private Question question;

    @Before
    public void setUp() {
        writer = new User(1, "tester", "passwd", "name", "tester@test.com");
        question = new Question("Question 제목", "본문 내용~~~");
        question.writeBy(writer);
    }

    @Test
    public void update_question_success() {
        // Given :: setUp
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        String updatedTitle = "수정한 제목";
        String updatedContents = "수정한 본문 내용";

        // When
        qnaService.update(writer, 1L, new Question(updatedTitle, updatedContents));

        // Then
        assertThat(question.getTitle()).isEqualTo(updatedTitle);
        assertThat(question.getContents()).isEqualTo(updatedContents);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_question_failed_when_not_owner() {
        // Given :: setUp
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        final User other = new User(2, "other", "passwd", "other", "other@test.com");

        // When
        qnaService.update(other, 1L, new Question("제목 수정!", "본문 수정 ~~"));

        // Then :: expected
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_question_failed_when_deleted() throws CannotDeleteException {

        // Given :: setUp
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        qnaService.deleteQuestion(writer, 1L);

        // When
        qnaService.update(writer, 1L, new Question("제목 수정!", "본문 수정 ~~"));

        // Then :: expected
    }

    @Test
    public void delete_question_success() throws CannotDeleteException {
        // Given :: setUp
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        // When
        qnaService.deleteQuestion(writer, 1L);

        // Then
        assertThat(question.isDeleted()).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_question_failed_when_not_owner() throws CannotDeleteException {
        // Given :: setUp
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        final User other = new User(2, "other", "passwd", "other", "other@test.com");

        // When
        qnaService.deleteQuestion(other, 1L);

        // Then :: expected
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_question_failed_already_deleted() throws CannotDeleteException {
        // Given :: setUp
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        question.delete(writer);

        // When
        qnaService.deleteQuestion(writer, 1L);

        // Then :: expected
    }

    @Test
    public void add_answer() {
        // Given :: setUp
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        User answerWriter = newUser("answriter");
        String answerContents = "comment~~~~";

        // When
        Answer answer = qnaService.addAnswer(answerWriter, 1L, answerContents);

        // Then
        assertThat(answer.getQuestion()).isEqualTo(question);
        assertThat(answer.getWriter()).isEqualTo(answerWriter);
    }

    @Test(expected = EntityNotFoundException.class)
    public void add_answer_to_not_found_question() {
        // Given :: setUp
        User answerWriter = newUser("answriter");
        String answerContents = "comment~~~~";

        // When
        Answer answer = qnaService.addAnswer(answerWriter, 2L, answerContents);
    }

    @Test
    public void delete_answer() {
        // Given :: setUp
        User answerWriter = newUser("answriter");
        String comment = "comment~~~~";
        Answer answer = new Answer(answerWriter, comment);
        answer.toQuestion(question);
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        // When
        qnaService.deleteAnswer(answerWriter, 1L);

        // Then
        assertThat(answer.isDeleted()).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_answer_not_owner() {
        // Given :: setUp
        User answerWriter = newUser(1L, "answriter", "test");
        String comment = "comment~~~~";
        Answer answer = new Answer(answerWriter, comment);
        answer.toQuestion(question);
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        // When
        qnaService.deleteAnswer(newUser(2L, "other", "tt"), 1L);
    }
}