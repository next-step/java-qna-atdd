package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class AnswerTest {

    private User javajigi = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    private User sanjigi = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");
    private Answer answer = new Answer(javajigi, "TEST_ANSWER");
    private String update = "updated answer";

    @Test
    public void 질문에_대한_답변_삭제() throws CannotDeleteException {
        answer.delete(javajigi);
        assertThat(answer.isDeleted()).isEqualTo(true);
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문에_대한_답변자가_아닌_사람이_삭제() throws CannotDeleteException {
        answer.delete(sanjigi);
    }

    @Test
    public void 질문에_대한_답변_수정() {
        answer.update(javajigi, update);
        assertThat(answer.getContents().equals(update)).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문에_대한_답변자가_아닌_사람이_수정() {
        answer.update(sanjigi, update);
    }
}