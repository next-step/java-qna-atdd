package nextstep.service;

import static org.mockito.Mockito.when;

import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import nextstep.UnAuthorizedException;
import nextstep.domain.Answer;
import nextstep.domain.AnswerRepository;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

@RunWith(MockitoJUnitRunner.Silent.class)
public class QnaServiceTest extends BaseTest {

  @Mock
  private QuestionRepository questionRepository;

  @Mock
  private AnswerRepository answerRepository;

  @InjectMocks
  private QnaService qnaService;

  @Test
  public void findById_success() throws Exception {

    User loginUser = new User("sanjigi", "password", "name", "javajigi@slipp.net");
    Question question = new Question("질문 제목", "질문 내용");
    question.setId(1L);
    question.writeBy(loginUser);
    when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

    Question findQuestion = qnaService.findById(loginUser, question.getId());
    softly.assertThat(findQuestion).isEqualTo(question);
  }

  @Test(expected = EntityNotFoundException.class)
  public void findById_failed_when_question_not_found() {

    User loginUser = new User("sanjigi", "password", "name", "javajigi@slipp.net");
    Question question = new Question("질문 제목", "질문 내용");
    question.writeBy(loginUser);
    question.setId(1L);

    when(questionRepository.findById(question.getId())).thenReturn(Optional.empty());

    qnaService.findById(loginUser, question.getId());
  }

  @Test(expected = UnAuthorizedException.class)
  public void findById_failed_when_not_owner() {

    User loginUser = new User("sanjigi", "password", "name", "javajigi@slipp.net");
    loginUser.setId(2L);

    User writer = new User("javajigi", "test", "자바지기", "javajigi@slipp.net");
    writer.setId(1L);

    Question question = new Question("질문 제목", "질문 내용");
    question.writeBy(writer);
    question.setId(1L);

    when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

    qnaService.findById(loginUser, question.getId());
  }

  @Test
  public void findAnswerById_success() throws Exception {

    // Given
    long questionId = 100L;
    long answerId = 200L;
    User writer = new User("sanjigi", "password", "name", "javajigi@slipp.net");

    Question question = new Question("질문 제목", "질문 내용");
    question.setId(questionId);

    Answer answer = new Answer(answerId, writer, question, "답변 내용");
    question.addAnswer(answer);
    when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
    when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));

    // When
    Answer findAnswer = qnaService.findAnswerById(questionId, answerId);

    // Then
    softly.assertThat(findAnswer).isEqualTo(answer);
    softly.assertThat(findAnswer.getQuestion()).isEqualTo(question);
  }

  @Test(expected = EntityNotFoundException.class)
  public void findAnswerById_notFound_question() throws Exception {

    // Given
    long questionId = 100L;
    long answerId = 200L;
    when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

    // When
    qnaService.findAnswerById(questionId, answerId);
  }

  @Test(expected = EntityNotFoundException.class)
  public void findAnswerById_notQuestion_answer() throws Exception {

    // Given
    long questionId = 100L;
    long answerId = 200L;

    Question question = new Question("질문 제목", "질문 내용");
    question.setId(questionId);
    when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

    // When
    qnaService.findAnswerById(questionId, answerId);
  }

  @Test(expected = EntityNotFoundException.class)
  public void findAnswerById_notFound_answer() throws Exception {

    // Given
    long questionId = 100L;
    long answerId = 200L;
    User writer = new User("sanjigi", "password", "name", "javajigi@slipp.net");

    Question question = new Question("질문 제목", "질문 내용");
    question.setId(questionId);

    Answer answer = new Answer(300L, writer, question, "답변 내용");
    question.addAnswer(answer);
    when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
    when(answerRepository.findById(answerId)).thenReturn(Optional.empty());

    // When
    qnaService.findAnswerById(questionId, answerId);
  }
}