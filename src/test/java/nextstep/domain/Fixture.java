package nextstep.domain;

public class Fixture {
    public static String title = "아기상어";
    public static String contents = "뚜루루뚜루";
    public static Question mockQuestion = new Question(title, contents);
    public static User mockUser = new User("sanjigi", "test", "name", "javajigi@slipp.net");
    public static Answer answer = new Answer(mockUser, "껀톈뚜");
}
