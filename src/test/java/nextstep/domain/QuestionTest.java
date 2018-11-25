package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import java.util.Arrays;

public class QuestionTest extends BaseTest {
    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");
    public static final Question question = new Question("TDD를 배우는 이유는?", "리팩토링 향상을 위해");

    public static Question newQuestion(String title, String contents, User user) {
        return new Question(0L ,title, contents, user ,Arrays.asList());
    }

    public static Question newQuestion(User user) {
        return newQuestion("title", "contents", user);
    }

    @Test
    public void update_question(){
        Question question = newQuestion("TDD란", "리팩토링", JAVAJIGI);
        Question target = newQuestion("TDD를 배우는 이유는?", "리팩토링 향상을 위해", JAVAJIGI);
        question.update(target);

        softly.assertThat(question.getTitle()).isEqualTo(target.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(target.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_question(){
        Question question = newQuestion(JAVAJIGI);
        Question target = newQuestion(SANJIGI);
        question.update(target);
    }



}
