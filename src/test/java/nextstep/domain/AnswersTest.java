package nextstep.domain;

import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class AnswersTest extends BaseTest {
    private Answers answers;

    @Before
    public void setUp() throws Exception {
        answers = new Answers();
    }

    @Test
    public void 생성() {
        answers.add(new Answer(UserTest.JAVAJIGI, "답변 내용"));
        answers.add(new Answer(UserTest.JAVAJIGI, "답변 내용2"));

        softly.assertThat(answers).isNotNull();
    }

    @Test
    public void 생성_같은사용자가_작성한_답변만_존재() {
        answers.add(new Answer(UserTest.JAVAJIGI, "답변 내용"));
        answers.add(new Answer(UserTest.JAVAJIGI, "답변 내용2"));

        softly.assertThat(answers.hasOtherUsersAnswer(UserTest.JAVAJIGI)).isFalse();
    }

    @Test
    public void 생성_다른사용자가_작성한_답변_존재() {
        answers.add(new Answer(UserTest.JAVAJIGI, "답변 내용"));
        answers.add(new Answer(UserTest.SANJIGI, "답변 내용2"));

        softly.assertThat(answers.hasOtherUsersAnswer(UserTest.SANJIGI)).isTrue();
    }
}