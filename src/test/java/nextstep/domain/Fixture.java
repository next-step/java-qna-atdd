package nextstep.domain;

public class Fixture {
    public static final String TITLE = "아기상어";
    public static String CONTENTS = "뚜루루뚜루";
    public static Question MOCK_QUESTION = Question.builder().title(TITLE).contents(CONTENTS).build();
    public static User MOCK_USER = new User(1L,"javajigi", "test", "자바지기", "javajigi@slipp.net");
    public static User OTHER_USER = new User(2L,"sanjigi", "test", "산지기", "sanjigi@slipp.net");
}
