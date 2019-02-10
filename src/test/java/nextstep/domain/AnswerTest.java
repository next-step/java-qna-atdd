package nextstep.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerTest {
    public static final User KITAEJIGI = new User(1L, "kitaejigi", "test", "kitae", "kitae@code.com");
    public static final User JIGIGUEMJI = new User(2L, "jigiguemji", "test", "hanul", "hanul@code.com");

    @Test
    public void 답변달기() {
        Question question = new Question("first Question", "add answer here");
        Answer answer = new Answer(KITAEJIGI, "add answer");
        question.addAnswer(answer);
        assertThat(answer.getQuestion()).isEqualTo(question);
    }

    @Test
    public void 답변수정() {
        Answer answer = new Answer(KITAEJIGI, "add answer");
        String modifyContent = "modify contents";
        Answer updatedAnswer = new Answer(KITAEJIGI, modifyContent);
        answer.update(KITAEJIGI, updatedAnswer);
        assertThat(answer.getContents()).isEqualTo(modifyContent);
    }

    @Test
    public void 답변삭제() {
        Question question = new Question("first Question", "add answer here");
        Answer answer = new Answer(KITAEJIGI, "add answer");
        question.addAnswer(answer);
        answer.delete(KITAEJIGI);
        assertThat(answer.isDeleted()).isTrue();
    }
}
