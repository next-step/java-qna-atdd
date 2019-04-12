package nextstep.domain;

import nextstep.exception.CannotDeleteException;
import org.junit.Test;
import support.test.BaseTest;

public class AnswerTest extends BaseTest {
    public static final User ONE = new User(3L, "crystal", "crystal", "크리스탈", "crystal@gmail.com");
    public static final User OTHER = new User(4L, "testuser", "testuser", "테스트", "test@gmail.com");

    public static Question question = new Question(0L, "제목이에요.", "내용이에요", ONE);
    public static Answer answer = new Answer(0L, ONE, question, "답변입니당");

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws Exception {
        // given
        // when
        answer.delete(OTHER);
        // then
    }

    @Test
    public void delete_owner() throws Exception {
        // given
        // when
        answer.delete(ONE);
        // then
        softly.assertThat(answer.isDeleted()).isTrue();
    }
}
