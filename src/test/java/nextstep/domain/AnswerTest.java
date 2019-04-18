package nextstep.domain;

import nextstep.exception.CannotDeleteException;
import org.junit.Test;
import support.test.BaseTest;

import static nextstep.domain.UserTest.ONE;
import static nextstep.domain.UserTest.OTHER;

public class AnswerTest extends BaseTest {
    public static Answer ONE_ANSWER = new Answer(ONE, "crystal의 답변");
    public static Answer OTHER_ANSWER = new Answer(OTHER, "testuser의 답변");

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws Exception {
        // given
        // when
        ONE_ANSWER.delete(OTHER);
        // then
    }

    @Test
    public void delete_owner() throws Exception {
        // given
        // when
        ONE_ANSWER.delete(ONE);
        // then
        softly.assertThat(ONE_ANSWER.isDeleted()).isTrue();
    }
}
