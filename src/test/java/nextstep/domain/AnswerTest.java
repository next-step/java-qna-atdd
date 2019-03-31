package nextstep.domain;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class AnswerTest extends BaseTest {

  @Test
  public void delete_success() {

    // Given
    User writer = new User("sanjigi", "password", "name", "javajigi@slipp.net");
    Answer answer = new Answer(writer, "답변 내용");

    // When
    answer.delete(writer);

    // Then
    softly.assertThat(answer.isDeleted()).isTrue();
  }

  @Test(expected = UnAuthorizedException.class)
  public void delete_notOwner() {

    // Given
    User loginUser = new User("sanjigi", "password", "name", "javajigi@slipp.net");

    User writer = new User("javajigi", "test", "자바지기", "javajigi@slipp.net");
    writer.setId(1L);
    Answer answer = new Answer(writer, "답변 내용");

    // When
    answer.delete(loginUser);
  }
}