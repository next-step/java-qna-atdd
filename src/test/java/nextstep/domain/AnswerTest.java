package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerTest {
    public final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    private Answer answer;

    @Before
    public void init(){
        answer = new Answer(JAVAJIGI, "TDD");
    }

    @Test
    public void 답변_입력() {
        Question question = new Question("TDD를 배우는 이유는?", "리팩토링 향상을 위해");
        answer.toQuestion(question);
        Answers answers = new Answers();
        answers.addAnswer(answer);

        assertThat(answer.getQuestion()).isEqualTo(question);
    }

    @Test
    public void 답변_삭제() throws Exception {
        answer.delete(JAVAJIGI);

        assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변_삭제_다른사용자() throws Exception {
        answer.delete(SANJIGI);
    }

    @Test(expected = CannotDeleteException.class)
    public void 답변_이미_삭제() throws Exception {
        answer.delete(JAVAJIGI);
        answer.delete(JAVAJIGI);
    }

}
