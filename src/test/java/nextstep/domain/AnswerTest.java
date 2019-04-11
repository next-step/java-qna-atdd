package nextstep.domain;

import nextstep.ForbiddenException;
import nextstep.NotFoundException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class AnswerTest extends BaseTest {
    private User writer = UserTest.newUser(1L);
    private Question question = QuestionTest.newQuestion(1L);

    public static Answer newAnswer(Long id) {
        return new Answer(id, new User(), new Question(), "answer");
    }

    @Test
    public void 답변을_생성한다() {
        Answer answer = new Answer(writer, question, "This is answer");

        softly.assertThat(answer.getWriter()).isEqualTo(writer);
        softly.assertThat(answer.getContents()).isEqualTo("This is answer");
    }

    @Test(expected = UnAuthorizedException.class)
    public void 작성자없이_답변을_달수없다() {
        new Answer(null, question, "This is title");
    }

    @Test(expected = NotFoundException.class)
    public void 없는_질문에_답변을_달수없다() {
        new Answer(writer, null, "This is contents");
    }

    @Test
    public void 작성자인지_확인한다() {
        Answer answer = new Answer(writer, question, "This is answer");
        softly.assertThat(answer.isOwner(writer)).isTrue();
    }

    @Test
    public void 답변을_삭제한다() {
        Answer answer = new Answer(writer, question, "This is answer");
        answer.delete(writer);

        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = ForbiddenException.class)
    public void 작성자가_아닌데_답변을_삭제하면_예외가_발생한다() {
        Answer answer = new Answer(writer, question, "This is answer");

        User otherUser = UserTest.newUser(2L);
        answer.delete(otherUser);
    }
}
