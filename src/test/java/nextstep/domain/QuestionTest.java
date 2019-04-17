package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class QuestionTest extends BaseTest {
    public static final Question ORIGINAL_QUESTION = new Question("title", "contents");
    public static final Question UPDATE_QUESTION = new Question("update title", "update contents");

    @Before
    public void initQuestion() {
        ORIGINAL_QUESTION.writeBy(UserTest.JAVAJIGI);
    }

    @Test
    public void update_owner() {
        Question updateQuestion = new Question("update title", "update contents");
        Question question = ORIGINAL_QUESTION.update(UserTest.JAVAJIGI, updateQuestion);
        assertThat(question.getTitle()).isEqualTo(updateQuestion.getTitle());
        assertThat(question.getContents()).isEqualTo(updateQuestion.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        Question updatedQuestion = new Question("testTitle", "testContent");
        ORIGINAL_QUESTION.update(UserTest.SANJIGI, updatedQuestion);
    }

    @Test
    public void delete_owner() throws CannotDeleteException {
        ORIGINAL_QUESTION.delete(UserTest.JAVAJIGI);
        assertThat(ORIGINAL_QUESTION.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws CannotDeleteException {
        ORIGINAL_QUESTION.delete(UserTest.SANJIGI);
        assertThat(ORIGINAL_QUESTION.isDeleted()).isTrue();
    }

    @Test
    public void is_now_owner() {
        assertThat(ORIGINAL_QUESTION.isOwner(UserTest.SANJIGI)).isFalse();
    }

    @Test
    public void is_owner() {
        assertThat(ORIGINAL_QUESTION.isOwner(UserTest.JAVAJIGI)).isTrue();
    }
}
