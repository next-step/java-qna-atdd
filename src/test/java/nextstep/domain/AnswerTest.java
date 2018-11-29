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

    private User createUser(final String userId, final String password, final String name, final String email) {
        return new User(userId, password, name, email);
    }

}