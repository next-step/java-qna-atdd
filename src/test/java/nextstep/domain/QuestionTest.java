package nextstep.domain;

import nextstep.CannotDeleteException;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class QuestionTest {
    public static final Question Q1 =
            new Question("title1", "contents1").writeBy(UserTest.JAVAJIGI);

    @Test
    public void delete_성공() throws Exception {
        Question question = new Question("title1", "contents1").writeBy(UserTest.JAVAJIGI);
        List<DeleteHistory> deleteHistories = question.delete(UserTest.JAVAJIGI);

        assertThat(question.isDeleted()).isTrue();
        assertThat(deleteHistories).hasSize(1);
    }

    @Test
    public void delete_다른_사람이_쓴_글() {
        Question question = new Question("title1", "contents1").writeBy(UserTest.JAVAJIGI);
        assertThatThrownBy(() -> {
            question.delete(UserTest.SANJIGI);
        }).isInstanceOf(CannotDeleteException.class);
    }

    @Test
    public void delete_성공_질문자_답변자_같음() throws Exception {
        Question question = new Question("title1", "contents1").writeBy(UserTest.JAVAJIGI);
        question.addAnswer(AnswerTest.A1);
        List<DeleteHistory> deleteHistories = question.delete(UserTest.JAVAJIGI);

        assertThat(question.isDeleted()).isTrue();
        assertThat(deleteHistories).hasSize(2);
    }

    @Test
    public void delete_답변_중_다른_사람이_쓴_글() {
        Question question = new Question("title1", "contents1").writeBy(UserTest.JAVAJIGI);
        assertThatThrownBy(() -> {
            question.addAnswer(AnswerTest.A1);
            question.addAnswer(AnswerTest.A2);
            question.delete(UserTest.JAVAJIGI);
        }).isInstanceOf(CannotDeleteException.class);
    }
}
