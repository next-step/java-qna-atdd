package nextstep.domain;

public class Fixture {
    public static final String TITLE = "아기상어";
    public static String CONTENTS = "뚜루루뚜루";
    public static Question MOCK_QUESTION = Question.builder().title(TITLE).contents(CONTENTS).build();
    public static User MOCK_USER = new User("javajigi", "test", "자바지기", "javajigi@slipp.net");
    public static User OTHER_USER = new User("sanjigi", "test", "산지기", "sanjigi@slipp.net");
    public static Answer ANSWER = new Answer(1L, MOCK_USER, MOCK_QUESTION, "껀톈뚜");
}
