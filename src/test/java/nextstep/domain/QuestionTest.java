package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings({"NonAsciiCharacters", "SpellCheckingInspection"})
public class QuestionTest {

    public static Question newQuestion(final String title, final String contents) {
        return new Question(title, contents);
    }

    @Test
    public void 작성자가_질문_수정() {
        final Question question1 = newQuestion("타이틀", "내용");
        final Question question2 = newQuestion("클린코드", "좋은 코드 만들어보자");
        final User write = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");
        question1.writeBy(write);
        question1.update(write, question2);
        assertThat(question1.eqTitleAndContents(question2)).isTrue();
    }

    @Test(expected = CannotUpdateException.class)
    public void 작성자가_삭제된_질문을_수정() {
        final Question question1 = new Question("타이틀", "내용");
        final Question question2 = new Question("클린코드", "좋은 코드 만들어보자");
        final User write = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");
        question1.writeBy(write);
        question1.delete(write);
        question1.update(write, question2);
    }

    @Test(expected = CannotUpdateException.class)
    public void 작성자가_아닌데_질문_수정() {
        final Question question1 = newQuestion("타이틀", "내용");
        final Question question2 = newQuestion("클린코드", "좋은 코드 만들어보자");
        final User write = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");
        final User newUser = createUser("javajigi", "test", "자바지기", "javajigi@slipp.net");
        question1.writeBy(write);
        question1.update(newUser, question2);
    }

    @Test
    public void 자신의_질문_삭제() {
        final Question question = newQuestion("타이틀", "내용");
        final User write = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");
        question.writeBy(write);
        question.delete(write);
        assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void 자신이_이미_삭제한_질문인데_다시_삭제() {
        final Question question = newQuestion("타이틀", "내용");
        final User write = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");
        question.writeBy(write);
        question.delete(write);
        question.delete(write);
    }

    @Test(expected = CannotDeleteException.class)
    public void 작성자가_아닌데_삭제() {
        final Question question = newQuestion("타이틀", "내용");
        final User write = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");
        final User newUser = createUser("javajigi", "test", "자바지기", "javajigi@slipp.net");
        question.writeBy(write);
        question.delete(newUser);
    }

    @Test
    public void 질문한_사람과_로그인한_사람이_같으면서_답변이_없는_경우_삭제_가능() {
        final Question question = newQuestion("타이틀", "내용");
        final User write = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");
        question.writeBy(write);
        question.delete(write);
        assertThat(question.isDeleted()).isTrue();
    }

    @Test
    public void 질문한_사람과_로그인한_사람이_같으면서_답변의_작성자가_같은_경우_삭제_가능() {

        final User loginUser = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");

        final Question question = newQuestion("타이틀", "내용");
        final User write = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");
        question.writeBy(write);
        question.addAnswer(new Answer(write, "좋은 책이네요.~"));
        question.addAnswer(new Answer(write, "좋은 책이네요.!"));
        assertThat(question.hasAnswers()).isTrue();

        question.delete(loginUser);
        assertThat(question.hasAnswers()).isFalse();
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문한_사람과_로그인한_사람이_같지만_답변의_작성자가_다른_같은_경우_삭제_가능하지_않음() {

        final User loginUser = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");

        final Question question = newQuestion("타이틀", "내용");
        final User write = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");
        final User newUser = createUser("javajigi", "test", "자바지기", "javajigi@slipp.net");
        question.writeBy(write);
        question.addAnswer(new Answer(write, "좋은 책이네요.~"));
        question.addAnswer(new Answer(newUser, "좋은 책이네요.!"));
        assertThat(question.hasAnswers()).isTrue();

        question.delete(loginUser);
    }

    @Test
    public void 질문한_사람과_로그인한_사람이_같으면서_답변의_작성자가_같은_경우_삭제_가능하고_삭제한_이력정보가_남아야한다() {

        final User loginUser = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");

        final Question question = newQuestion("타이틀", "내용");
        final User write = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");
        question.writeBy(write);
        question.addAnswer(new Answer(write, "좋은 책이네요.~"));
        assertThat(question.hasAnswers()).isTrue();

        final List<DeleteHistory> deleteHistories = question.delete(loginUser);
        assertThat(deleteHistories.size()).isEqualTo(2);
        assertThat(question.hasAnswers()).isFalse();
    }

    private User createUser(final String userId, final String password, final String name, final String email) {
        return new User(userId, password, name, email);
    }

}