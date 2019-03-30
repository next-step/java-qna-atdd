package nextstep.service;

import static org.mockito.Mockito.when;

import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import nextstep.UnAuthorizedException;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
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
}