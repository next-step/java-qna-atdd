package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import org.junit.Test;

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

    private User createUser(final String userId, final String password, final String name, final String email) {
        return new User(userId, password, name, email);
    }

}