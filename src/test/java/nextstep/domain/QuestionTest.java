package nextstep.domain;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {

  @Test
  public void update_success() {

    // Given
    User loginUser = new User("sanjigi", "password", "name", "javajigi@slipp.net");
    Question question = new Question("질문 제목", "질문 내용");
    question.writeBy(loginUser);

    String updateTitle = "질문 제목 수정";
    String updateContents = "질문 내용 수정";
    Question target = new Question(updateTitle, updateContents);

    // When
    question.update(loginUser, target);

    // Then
    softly.assertThat(question.getTitle()).isEqualTo(updateTitle);
    softly.assertThat(question.getContents()).isEqualTo(updateContents);
  }

  @Test(expected = UnAuthorizedException.class)
  public void update_notOwner() {

    // Given
    User loginUser = new User("sanjigi", "password", "name", "javajigi@slipp.net");

    User writer = new User("javajigi", "test", "자바지기", "javajigi@slipp.net");
    writer.setId(1L);
    Question question = new Question("질문 제목", "질문 내용");
    question.writeBy(writer);

    String updateTitle = "질문 제목 수정";
    String updateContents = "질문 내용 수정";
    Question target = new Question(updateTitle, updateContents);

    // When
    question.update(loginUser, target);
  }

  @Test
  public void delete_success() {

    // Given
    User loginUser = new User("sanjigi", "password", "name", "javajigi@slipp.net");
    Question question = new Question("질문 제목", "질문 내용");
    question.writeBy(loginUser);

    // When
    question.delete(loginUser);

    // Then
    softly.assertThat(question.isDeleted()).isTrue();
  }

  @Test(expected = UnAuthorizedException.class)
  public void delete_notOwner() {

    // Given
    User loginUser = new User("sanjigi", "password", "name", "javajigi@slipp.net");

    User writer = new User("javajigi", "test", "자바지기", "javajigi@slipp.net");
    writer.setId(1L);
    Question question = new Question("질문 제목", "질문 내용");
    question.writeBy(writer);

    // When
    question.delete(loginUser);
  }

  public static Question newQuestion(String title, String content) {
    return new Question(title, content);
  }

  public static Question editQuestion(long id, String title, String content) {
    Question editQuestion = new Question(title, content);
    editQuestion.setId(id);
    return editQuestion;
  }
}