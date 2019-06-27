package nextstep.domain;

import nextstep.CannotDeleteException;
import org.junit.Test;
import support.test.BaseTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AnswerTest extends BaseTest {
    public static final Answer A1 = new Answer(UserTest.JAVAJIGI, QuestionTest.Q1, "Answers Contents1");
    public static final Answer A2 = new Answer(UserTest.SANJIGI, QuestionTest.Q1, "Answers Contents2");

    @Test
    public void delete_성공() throws Exception {
        Answer answer = new Answer(UserTest.JAVAJIGI, QuestionTest.Q1, "Answers Contents1");
        answer.delete(UserTest.JAVAJIGI);

        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test
    public void delete_다른_사람_답변() {
        Answer answer = new Answer(UserTest.JAVAJIGI, QuestionTest.Q1, "Answers Contents1");
        assertThatThrownBy(() -> {
            answer.delete(UserTest.SANJIGI);
        }).isInstanceOf(CannotDeleteException.class);
    }
}
