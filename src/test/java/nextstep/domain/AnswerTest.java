package nextstep.domain;

import org.junit.Test;
import support.test.BaseTest;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerTest extends BaseTest {
    private Question question;

    @Test
    public void createAnswerTest() {
        //given
        User testUser = basicUser;
        String testContents = "answerContents";

        // when
        Answer answer = new Answer(testUser, testContents);

        // then
        assertThat(answer.getContents()).isEqualTo(testContents);
        assertThat(answer.getWriter()).isEqualTo(basicUser);
        assertThat(answer.isDeleted()).isFalse();
    }

    @Test
    public void toQuestionTest() {
        // given
        Question firstQuestion = new Question("aaa", "aaa", basicUser);
        Question secondQuestion = new Question("bbb", "bbb", basicUser);

        Answer targetAnswer = new Answer(basicUser, "contents");

        firstQuestion.addAnswer(targetAnswer);

        assertThat(targetAnswer.getQuestion()).isEqualTo(firstQuestion);

        // when
        targetAnswer.toQuestion(secondQuestion);

        //then
        assertThat(targetAnswer.getQuestion()).isEqualTo(secondQuestion);
    }

    @Test
    public void isOwnerTest_정상케이스() {
        // given
        Answer targetAnswer = new Answer(basicUser, "contents");

        //then
        assertThat(targetAnswer.isOwner(basicUser)).isTrue();
    }

    @Test
    public void isOwnerTest_실패케이스() {
        // given
        Answer targetAnswer = new Answer(basicUser, "contents");

        //then
        assertThat(targetAnswer.isOwner(anotherUser)).isFalse();
    }

}