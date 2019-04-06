package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class QuestionTest extends BaseTest {
    private Question question;

    @Before
    public void initQuestion() {
        this.question = new Question("test title", "test contents");
        question.writeBy(UserTest.JAVAJIGI);
    }

    @Test
    public void update_owner() {
        Question updateQuestion = new Question("update title", "update contents");
        Question question = this.question.update(UserTest.JAVAJIGI, updateQuestion);
        assertThat(question.getTitle()).isEqualTo(updateQuestion.getTitle());
        assertThat(question.getContents()).isEqualTo(updateQuestion.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        Question updatedQuestion = new Question("testTitle", "testContent");
        this.question.update(UserTest.SANJIGI, updatedQuestion);
    }

    @Test
    public void delete_owner() throws CannotDeleteException {
        this.question.delete(UserTest.JAVAJIGI);
        assertThat(this.question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws CannotDeleteException {
        this.question.delete(UserTest.SANJIGI);
        assertThat(this.question.isDeleted()).isTrue();
    }

    @Test
    public void is_now_owner() {
        assertThat(this.question.isOwner(UserTest.SANJIGI)).isFalse();
    }

    @Test
    public void is_owner() {
        assertThat(this.question.isOwner(UserTest.JAVAJIGI)).isTrue();
    }
}
