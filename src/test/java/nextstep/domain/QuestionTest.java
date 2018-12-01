package nextstep.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest {
    public static final User KITAEJIGI = new User(1L, "kitaejigi", "test", "kitae", "kitae@code.com");
    public static final User JIGIGUEMJI = new User(2L, "jigiguemji", "test", "hanul", "hanul@code.com");

    @Test
    public void update() {
        Question question = new Question("제목", "내용");
        question.writeBy(KITAEJIGI);
        Question updatedQuestion = new Question("제목", "내용수정");
        updatedQuestion.writeBy(KITAEJIGI);
        assertThat(question.update(updatedQuestion)).isEqualTo(updatedQuestion);
    }

    @Test
    public void delete() {
        Question question = new Question("제목", "내용");
        question.writeBy(KITAEJIGI);
        question.delete(KITAEJIGI);
        assertThat(question.isDeleted()).isTrue();
    }
}
