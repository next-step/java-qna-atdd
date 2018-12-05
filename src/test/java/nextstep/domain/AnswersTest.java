package nextstep.domain;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswersTest {

    Answers answers;

    @Before
    public void setUp() {
        answers = new Answers();
    }

    @Test
    public void match_writer() {
        Answer answer1 = new Answer(UserTest.JAVAJIGI, "default 답변1");
        Answer answer2 = new Answer(UserTest.JAVAJIGI, "default 답변2");
        answers.addAnswer(answer1);
        answers.addAnswer(answer2);
        assertThat(answers.isMatchedWriter(UserTest.JAVAJIGI)).isTrue();
    }

    @Test
    public void not_match_writer() {
        Answer answer1 = new Answer(UserTest.JAVAJIGI, "default 답변1");
        Answer answer2 = new Answer(UserTest.SANJIGI, "default 답변2");
        answers.addAnswer(answer1);
        answers.addAnswer(answer2);
        assertThat(answers.isMatchedWriter(UserTest.JAVAJIGI)).isFalse();
    }

}