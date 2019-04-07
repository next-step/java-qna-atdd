package nextstep.domain;

import nextstep.UnAuthorizedException;
import nextstep.domain.entity.Answer;
import nextstep.domain.entity.Question;
import nextstep.domain.entity.User;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerTest {

    private User answerWriter;
    private User questionWriter;
    private Question question;

    @Before
    public void setUp() throws Exception {
        answerWriter = new User(1, "userId","password","name,", "gmail@mail.com");
        questionWriter = new User(2, "guest","password","guest,", "gmail@mail.com");
        question = new Question("title", "content");
    }

    @Test
    public void create() {
        Answer answer = new Answer(1L, answerWriter, question, "답변");
        assertThat(answer).isNotNull();
    }

    @Test
    public void mapping_question() {
        Answer answer = new Answer(answerWriter, "답변");
        answer.toQuestion(question);
        assertThat(answer).isNotNull();
    }

    //onwer check
    @Test
    public void isOnwer() {
        Answer answer = new Answer(1L, answerWriter, question, "답변");
        assertThat(answer.isOwner(questionWriter)).isEqualTo(false);
    }

    //update
    @Test
    public void update() {
        Answer answer = new Answer(1L, answerWriter, question, "답변");
        Answer newAnswer = new Answer(answerWriter, "답변");
        answer.update(answerWriter, newAnswer);

        assertThat(answer.getContents()).isEqualTo(newAnswer.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_다른사용자() {
        Answer answer = new Answer(1L, answerWriter, question, "답변");
        Answer newAnswer = new Answer(answerWriter, "답변");

        answer.update(questionWriter, newAnswer);
    }

    //delete
    @Test
    public void delete() {
        Answer answer = new Answer(1L, answerWriter, question, "답변");
        answer.delete(answerWriter);
        assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_다른사용자() {
        Answer answer = new Answer(1L, answerWriter, question, "답변");
        answer.delete(questionWriter);
        assertThat(answer.isDeleted()).isTrue();
    }
}