package nextstep.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest {

    @Test
    public void update() {
        Question question = new Question("제목", "내용");
        Question updatedQuestion = new Question("제목", "내용수정");
        assertThat(question.update(updatedQuestion)).isEqualTo(updatedQuestion);
    }

    @Test
    public void delete() {
        Question question = new Question("제목", "내용");
        question.delete();
        assertThat(question.isDeleted()).isTrue();
    }
}
