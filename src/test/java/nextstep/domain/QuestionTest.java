package nextstep.domain;

import nextstep.exception.CannotDeleteException;
import nextstep.exception.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    public static final User ONE = new User(3L, "crystal", "crystal", "크리스탈", "crystal@gmail.com");
    public static final User OTHER = new User(4L, "testuser", "testuser", "테스트", "test@gmail.com");

    public static Question origin = new Question(0L, "제목이에요.", "내용이에요", ONE);
    public static Question target = new Question("제목이다", "내용이다");

    @Test
    public void update_owner() throws Exception {
        // given
        Question originQuestion = origin;
        // when
        originQuestion.update(ONE, target);
        // then
        softly.assertThat(origin.getTitle()).isEqualTo(target.getTitle());
        softly.assertThat(origin.getContents()).isEqualTo(target.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws Exception {
        // given
        Question originQuestion = origin;
        // when
//        System.out.println(OTHER);
        originQuestion.update(OTHER, target);
        // then
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws Exception {
        // given
        Question originQuestion = origin;
        // when
        originQuestion.delete(OTHER);
        // then
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_already_deleted() throws Exception {
        // given
        Question originQuestion = origin;
        originQuestion.delete(ONE);
        // when
        originQuestion.delete(ONE);
        // then
    }

    @Test
    public void delete_owner() throws Exception {
        // given
        Question originQuestion = origin;
        // when
        originQuestion.delete(ONE);
        // then
        softly.assertThat(originQuestion.isDeleted()).isTrue();
    }
}
