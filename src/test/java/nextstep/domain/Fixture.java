package nextstep.domain;

public class Fixture {
    public static String title = "아기상어";
    public static String contents = "뚜루루뚜루";
    public static Question mockQuestion = new Question(title, contents);
    public static User mockUser = new User("sanjigi", "test", "산지기", "sanjigi@slipp.net");
    public static Answer answer = new Answer(1L, mockUser, mockQuestion, "껀톈뚜");
}
