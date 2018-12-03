package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import java.util.List;

import static nextstep.domain.UserTest.JAVAJIGI;
import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest extends BaseTest {
    public static final String TITLE = "테스트 타이틀";
    public static final String CONTENTS = "테스트 컨텐츠";
    private static final User NEW_USER = UserTest.newUser("mirrors89");


    public static Question newQuestion(QuestionBody questionBody) {
        return newQuestion(questionBody, JAVAJIGI);
    }

    public static Question newQuestion(QuestionBody questionBody, User user) {
        Question question = new Question(questionBody);
        question.writeBy(user);

        return question;
    }

    @Test
    public void create() {
        User newUser = UserTest.newUser("mirrors89");

        QuestionBody questionBody = new QuestionBody(TITLE, CONTENTS);
        Question question = newQuestion(questionBody, newUser);

        assertThat(question.getQuestionBody()).isEqualTo(questionBody);
        assertThat(question.isOwner(newUser)).isEqualTo(true);
        assertThat(question.isDeleted()).isEqualTo(false);
    }

    @Test
    public void update_writer() {
        User newUser = UserTest.newUser("mirrors89");
        QuestionBody questionBody = new QuestionBody(TITLE, CONTENTS);
        Question question = newQuestion(questionBody, newUser);

        String updateTitle = "수정된 타이틀";
        String updateContents = "수정된 컨텐츠";
        QuestionBody updateQuestionBody = new QuestionBody(updateTitle, updateContents);

        question.update(newUser, updateQuestionBody);

        assertThat(question.getQuestionBody()).isEqualTo(updateQuestionBody);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_writer() {
        User anotherUser = UserTest.newUser("anotherUser");
        QuestionBody questionBody = new QuestionBody(TITLE, CONTENTS);
        Question question = newQuestion(questionBody, NEW_USER);

        String updateTitle = "수정된 타이틀";
        String updateContents = "수정된 컨텐츠";
        QuestionBody updateQuestionBody = new QuestionBody(updateTitle, updateContents);

        question.update(anotherUser, updateQuestionBody);
    }

    @Test
    public void delete_writer() throws CannotDeleteException {
        QuestionBody questionBody = new QuestionBody(TITLE, CONTENTS);
        Question question = newQuestion(questionBody, NEW_USER);

        question.delete(NEW_USER);
        assertThat(question.isDeleted()).isEqualTo(true);
    }


    @Test(expected = CannotDeleteException.class)
    public void delete_not_writer() throws CannotDeleteException {
        User anotherUser = UserTest.newUser("anotherUser");

        QuestionBody questionBody = new QuestionBody(TITLE, CONTENTS);
        Question question = newQuestion(questionBody, NEW_USER);

        question.delete(anotherUser);
    }

    @Test
    public void delete_question_and_answer_writer_login() throws CannotDeleteException {
        QuestionBody questionBody = new QuestionBody(TITLE, CONTENTS);
        Question question = newQuestion(questionBody, NEW_USER);

        question.addAnswer(new Answer(NEW_USER, CONTENTS));
        question.addAnswer(new Answer(NEW_USER, CONTENTS));

        List<DeleteHistory> deleteHistories = question.delete(NEW_USER);
        assertThat(question.isDeleted()).isEqualTo(true);
        assertThat(deleteHistories.size()).isEqualTo(3);

    }

    @Test(expected = CannotDeleteException.class)
    public void delete_question_and_answer_another_login() throws CannotDeleteException {
        QuestionBody questionBody = new QuestionBody(TITLE, CONTENTS);
        Question question = newQuestion(questionBody, NEW_USER);

        question.addAnswer(new Answer(JAVAJIGI, CONTENTS));
        question.addAnswer(new Answer(NEW_USER, CONTENTS));

        question.delete(NEW_USER);
    }
}
