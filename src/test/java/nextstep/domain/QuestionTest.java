package nextstep.domain;

import nextstep.web.exception.ForbiddenException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    private User loginUser;
    private Question question;

    @Before
    public void setUp() throws Exception {
         loginUser = new User(1L, "myId", "myPassword", "myName", "myEmail");
         question = new Question("This is title", "This is contents");
         question.writeBy(loginUser);
    }

    @Test
    public void 작성자가_맞는지_확인한다() {
        softly.assertThat(question.isOwner(loginUser)).isTrue();
    }

    @Test
    public void 질문을_수정한다() {
        Question updatedQuestion = new Question("This is updated title", "This is updated contents");
        question.update(loginUser, updatedQuestion);

        softly.assertThat(question.getTitle()).isEqualTo(updatedQuestion.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(updatedQuestion.getContents());
    }

    @Test(expected = ForbiddenException.class)
    public void 작성자가_아닌데_질문을_수정하면_예외가_발생한다() {
        Question updatedQuestion = new Question("This is updated title", "This is updated contents");
        User anotherUser = new User(2L, "yourId", "yourPassword", "yourName", "yourEmail");

        question.update(anotherUser, updatedQuestion);
    }

    @Test
    public void 잘뮨울_삭제한다() {
        question.delete(loginUser);

        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = ForbiddenException.class)
    public void 작성자가_아닌데_질문을_삭제하면_예외가_발생한다() {
        User anotherUser = new User(2L, "yourId", "yourPassword", "yourName", "yourEmail");

        question.delete(anotherUser);
    }
}
