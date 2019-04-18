package nextstep.domain;

import nextstep.exception.CannotDeleteException;
import nextstep.exception.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static nextstep.domain.AnswerTest.ONE_ANSWER;
import static nextstep.domain.AnswerTest.OTHER_ANSWER;
import static nextstep.domain.UserTest.ONE;
import static nextstep.domain.UserTest.OTHER;

public class QuestionTest extends BaseTest {
    public static Question origin = new Question(0L, "제목이에요.", "내용이에요", ONE);
    public static QuestionBody target = new QuestionBody("제목이다", "내용이다");

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
    public void add_answer() {
        // given
        Question originQuestion = origin;
        // when
        originQuestion.addAnswer(ONE_ANSWER);
        // then
        softly.assertThat(originQuestion.hasAnswer(ONE_ANSWER)).isTrue();
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
        originQuestion.addAnswer(ONE_ANSWER);
        // when
        originQuestion.delete(ONE);
        // then
        softly.assertThat(originQuestion.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_question_owner_is_not_same_answer_owner() throws Exception {
        // given
        Question originQuestion = origin;
        originQuestion.addAnswer(OTHER_ANSWER);
        // when
        originQuestion.delete(ONE);
        // then
        softly.assertThat(originQuestion.isDeleted()).isTrue();
    }
}
