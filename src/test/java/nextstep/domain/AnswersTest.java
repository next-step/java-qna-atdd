package nextstep.domain;

import static org.junit.Assert.*;

import org.junit.Test;
import support.test.BaseTest;

public class AnswersTest extends BaseTest {

  @Test
  public void isContain() {

    // Given
    long answerId = 100L;

    User loginUser = new User("sanjigi", "password", "name", "javajigi@slipp.net");

    Answers answers = new Answers();
    Answer answer = new Answer(answerId, loginUser, null, "답변 내용");
    answers.add(answer);

    // When
    boolean result = answers.isContain(answerId);

    // Then
    softly.assertThat(result).isTrue();
  }

  @Test
  public void isNotContain() {

    // Given
    long answerId = 100L;
    Answers answers = new Answers();

    // When
    boolean result = answers.isContain(answerId);

    // Then
    softly.assertThat(result).isFalse();
  }
}