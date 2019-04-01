package nextstep.domain;

import static nextstep.domain.UserTest.newUser;
import static org.assertj.core.api.Assertions.assertThat;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;

public class AnswerTest {
    public static final User answerWriter = newUser(1L);

    @Test
    public void update_owner() {
        Answer answer = new Answer(answerWriter, "contents...");
        String updatedContents = "update~~~~~~";

        answer.update(answerWriter, updatedContents);

        assertThat(answer.getContents()).isEqualTo(updatedContents);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        Answer answer = new Answer(answerWriter, "contents...");
        String updatedContents = "update~~~~~~";

        answer.update(newUser(2L), updatedContents);
    }

    @Test
    public void delete_owner() throws CannotDeleteException {
        Answer answer = new Answer(answerWriter, "contents...");
        answer.delete(answerWriter);

        assertThat(answer.isDeleted()).isEqualTo(true);
     }

    @Test(expected = UnAuthorizedException.class)
    public void delete_not_owner() throws CannotDeleteException {
        Answer answer = new Answer(answerWriter, "contents...");
        answer.delete(newUser(2L));
    }
}