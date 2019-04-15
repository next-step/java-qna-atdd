package nextstep.domain;

import nextstep.exception.CannotDeleteException;
import nextstep.exception.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    public static final User ONE = new User(3L, "crystal", "crystal", "크리스탈", "crystal@gmail.com");
    public static final User OTHER = new User(4L, "testuser", "testuser", "테스트", "test@gmail.com");

    public static Question origin;
    public static QuestionBody target = new QuestionBody("제목이다", "내용이다");

    public static Answer answerWrittenByOne = new Answer(ONE, "crystal의 답변");
    public static Answer answerWrittenByOther = new Answer(OTHER, "testuser의 답변");

    @Before
    public void setUp() throws Exception {
        origin = new Question(0L, "제목이에요.", "내용이에요", ONE);
    }

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
    public void delete_owner_no_answer() throws Exception {
        // given
        Question originQuestion = origin;
        // when
        originQuestion.delete(ONE);
        // then
        softly.assertThat(originQuestion.isDeleted()).isTrue();
    }

    @Test
    public void delete_question_owner_is_same_answer_owner() throws Exception {
        // given
        Question originQuestion = origin;
        originQuestion.addAnswer(answerWrittenByOne);
        // when
        originQuestion.delete(ONE);
        // then
        softly.assertThat(originQuestion.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_question_owner_is_not_same_answer_owner() throws Exception {
        // given
        Question originQuestion = origin;
        originQuestion.addAnswer(answerWrittenByOther);
        // when
        originQuestion.delete(ONE);
        // then
        softly.assertThat(originQuestion.isDeleted()).isTrue();
    }
}
