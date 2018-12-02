package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import org.junit.Test;

import static nextstep.domain.QuestionTest.newQuestion;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings({"NonAsciiCharacters", "SpellCheckingInspection"})
public class AnswerTest {

    @Test
    public void 답변_등록() {

        final Question question = newQuestion("타이틀", "내용");
        final User write = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");
        final Answer answer = new Answer(write, "좋은 책이네요.");
        assertThat(question.hasAnswers()).isFalse();

        question.addAnswer(answer);
        assertThat(question.hasAnswers()).isTrue();
    }

    @Test
    public void 답변_수정() {

        final Question question = newQuestion("타이틀", "내용");
        final User write = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");
        final Answer answer = new Answer(write, "좋은 책이네요.");
        assertThat(question.hasAnswers()).isFalse();

        question.addAnswer(answer);
        assertThat(question.hasAnswers()).isTrue();

        final Answer updateAnswer = new Answer(write, "좋은 책이네요^_^");
        answer.update(write, updateAnswer);
        assertThat(answer.eqContents(updateAnswer)).isTrue();
    }

    @Test(expected = CannotUpdateException.class)
    public void 다른_사람의_답변_수정() {

        final Question question = newQuestion("타이틀", "내용");
        final User write = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");
        final Answer answer = new Answer(write, "좋은 책이네요.");
        assertThat(question.hasAnswers()).isFalse();

        question.addAnswer(answer);
        assertThat(question.hasAnswers()).isTrue();

        final Answer updateAnswer = new Answer(write, "좋은 책이네요^_^");
        answer.update(createUser("javajigi", "test", "자바지기", "javajigi@slipp.net"), updateAnswer);
        assertThat(answer.eqContents(updateAnswer)).isTrue();
    }

    @Test
    public void 자신의_답변_삭제() {

        final Question question = newQuestion("타이틀", "내용");
        final User write = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");
        final Answer answer = new Answer(write, "좋은 책이네요.");
        assertThat(question.hasAnswers()).isFalse();

        question.addAnswer(answer);
        assertThat(question.hasAnswers()).isTrue();

        answer.delete(write);
        assertThat(question.hasAnswers()).isFalse();
        assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void 다른_사람의_답변_삭제() {

        final Question question = newQuestion("타이틀", "내용");
        final User write = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");
        final Answer answer = new Answer(write, "좋은 책이네요.");
        assertThat(question.hasAnswers()).isFalse();

        question.addAnswer(answer);
        assertThat(question.hasAnswers()).isTrue();

        answer.delete(createUser("javajigi", "test", "자바지기", "javajigi@slipp.net"));
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문한_사람과_로그인한_사람이_같지만_답변의_글쓴이가_다른_경우_삭제_가능하지_않음() {

        final User loginUser = createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com");

        final Question question = newQuestion("타이틀", "내용");
        final User newUser = createUser("javajigi", "test", "자바지기", "javajigi@slipp.net");
        final Answer answer = new Answer(newUser, "좋은 책이네요.");
        question.writeBy(createUser("ninezero90hy", "ninezero90hy@", "ninezero", "ninezero90hy@gmail.com"));
        question.addAnswer(answer);

        answer.delete(loginUser);
    }

    private User createUser(final String userId, final String password, final String name, final String email) {
        return new User(userId, password, name, email);
    }

}