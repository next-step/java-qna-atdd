package nextstep.domain;

public class QuestionTest {

    public static Question newQuestion(final String title, final String contents) {
        return new Question(title, contents);
    }

}