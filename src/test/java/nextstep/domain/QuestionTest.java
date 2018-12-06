package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;

public class QuestionTest extends BaseTest {
    public static final Question ONE_QUESTION = new Question("question_one", "질문있어요!");
    public static final Question UPDATE__QUESTION = new Question("update_question", "새로운 질문입니다.");
    public static final User STRANGER = new User(1L, "stranger", "password", "name", "stranger@slipp.net");

    @Test
    public void match_owner_correct() {
        ONE_QUESTION.writeBy(JAVAJIGI);
        softly.assertThat(ONE_QUESTION.isOwner(JAVAJIGI)).isTrue();
    }

    @Test
    public void match_owner_notCorrect() {
        ONE_QUESTION.writeBy(JAVAJIGI);
        softly.assertThat(ONE_QUESTION.isOwner(SANJIGI)).isFalse();
    }

    @Test
    public void update_with_owner() {
        ONE_QUESTION.writeBy(JAVAJIGI);
        ONE_QUESTION.update(JAVAJIGI, UPDATE__QUESTION);
        softly.assertThat(ONE_QUESTION.getTitle()).isEqualTo(UPDATE__QUESTION.getTitle());
        softly.assertThat(ONE_QUESTION.getContents()).isEqualTo(UPDATE__QUESTION.getContents());

    }

    @Test(expected = UnAuthorizedException.class)
    public void update_with_otherUser() {
        ONE_QUESTION.writeBy(JAVAJIGI);
        ONE_QUESTION.update(STRANGER, UPDATE__QUESTION);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_with_notLogin() {
        ONE_QUESTION.writeBy(JAVAJIGI);
        ONE_QUESTION.update(new User(), UPDATE__QUESTION);
    }

    @Test
    public void delete_with_owner() {
        ONE_QUESTION.writeBy(JAVAJIGI);
        ONE_QUESTION.delete(JAVAJIGI);
        softly.assertThat(ONE_QUESTION.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_with_otherUser() {
        ONE_QUESTION.writeBy(JAVAJIGI);
        ONE_QUESTION.delete(STRANGER);

    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_with_notLogin() {
        ONE_QUESTION.writeBy(JAVAJIGI);
        ONE_QUESTION.delete(new User());
    }

    @Test
    public void isOwner() {
        ONE_QUESTION.writeBy(JAVAJIGI);
        softly.assertThat(ONE_QUESTION.isOwner(STRANGER)).isFalse();
    }


}
