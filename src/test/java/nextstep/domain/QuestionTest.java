package nextstep.domain;

import nextstep.ForbiddenException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    private User writer = UserTest.newUser(1L);

    public static Question newQuestion(Long id) {
        return new Question(id, UserTest.newUser(1L), new QuestionBody("This is title", "This is contents"));
    }

    public static Question newQuestion(Long id, User user) {
        return new Question(id, user, new QuestionBody("This is title", "This is contents"));
    }

    @Test
    public void 질문을_생성한다() {
        QuestionBody questionBody = new QuestionBody("This is title", "This is contents");
        Question question = new Question(writer, questionBody);

        softly.assertThat(question.getWriter()).isEqualTo(writer);
        softly.assertThat(question.getQuestionBody()).isEqualTo(questionBody);
    }

    @Test
    public void 작성자가_맞는지_확인한다() {
        QuestionBody questionBody = new QuestionBody("This is title", "This is contents");
        Question question = new Question(writer, questionBody);

        softly.assertThat(question.isOwner(writer)).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 작성자가_없이_질문을_생성하면_예외가_발생한다() {
        QuestionBody questionBody = new QuestionBody("This is title", "This is contents");
        new Question(null, questionBody);
    }

    @Test
    public void 질문을_수정한다() {
        QuestionBody questionBody = new QuestionBody("This is title", "This is contents");
        Question question = new Question(writer, questionBody);

        QuestionBody newQuestionBody = new QuestionBody("This is updated title", "This is updated contents");
        question.update(writer, newQuestionBody);

        softly.assertThat(question.getQuestionBody()).isEqualTo(newQuestionBody);
    }

    @Test(expected = ForbiddenException.class)
    public void 작성자가_아닌데_질문을_수정하면_예외가_발생한다() {
        QuestionBody questionBody = new QuestionBody("This is title", "This is contents");
        Question question = new Question(writer, questionBody);

        QuestionBody newQuestionBody = new QuestionBody("This is updated title", "This is updated contents");
        User anotherWriter = UserTest.newUser(2L);
        question.update(anotherWriter, newQuestionBody);
    }

    @Test
    public void 질문울_삭제한다() {
        QuestionBody questionBody = new QuestionBody("This is title", "This is contents");
        Question question = new Question(writer, questionBody);

        question.delete(writer);

        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = ForbiddenException.class)
    public void 작성자가_아닌데_질문을_삭제하면_예외가_발생한다() {
        QuestionBody questionBody = new QuestionBody("This is title", "This is contents");
        Question question = new Question(writer, questionBody);

        User anotherWriter = UserTest.newUser(2L);
        question.delete(anotherWriter);
    }
}
