package nextstep.domain;

import org.junit.Test;
import support.test.BaseTest;

import static nextstep.domain.UserTest.JAVAJIGI;

public class QuestionTest extends BaseTest {
    public static final Question QUESTION_ONE = new Question("question_one","질문있어요!");

    @Test
    public void match_writer(){
        QUESTION_ONE.writeBy(JAVAJIGI);
        softly.assertThat(QUESTION_ONE.getWriter()).isEqualTo(JAVAJIGI);
    }

    @Test
    public void match_owner(){
        QUESTION_ONE.writeBy(JAVAJIGI);
        softly.assertThat(QUESTION_ONE.isOwner(JAVAJIGI)).isTrue();
    }



}
