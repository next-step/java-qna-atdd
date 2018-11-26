package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;


public class QuestionTest extends BaseTest {
    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");


    public static final Question QUESTION = Question.ofUser("정석질문갑니다.", "개발 잘하고싶은데", JAVAJIGI);
    public static final Question QUESTION1 = Question.ofUser("정석질문갑니다.22", "개발 잘하고싶은데22", SANJIGI);

    public static final Question newQuestion() {
        return Question.ofUser("지질하다", "내용이 엉?", JAVAJIGI);
    }


    public static User newUser(Long id) {
        return new User(id, "userId", "pass", "name", "javajigi@slipp.net");
    }

    public static User newUser(String userId) {
        return newUser(userId, "password");
    }

    public static User newUser(String userId, String password) {
        return new User(0L, userId, password, "name", "javajigi@slipp.net");
    }

    public static final Answer ANSWER = new Answer(1L, JAVAJIGI, QUESTION, "답변입니다. 좋은 답변이죠?");
    public static final Answer ANSWER1 = new Answer(2L, JAVAJIGI, QUESTION, "답변입니다. 좋은 답변이죠?");

    public static Answer newAnswer(Long id) {
        return new Answer(id, JAVAJIGI, QUESTION, "새로만든 답변");
    }

    @Test
    public void 질문하기() {
        Question question = newQuestion();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문하기_로그아웃() {
        Question question = newQuestion();
        question.writeBy(null);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문하기_손님() {
        Question question = newQuestion();
        question.writeBy(User.GUEST_USER);
    }

    @Test
    public void 질문업데이트() {
        Question question = newQuestion();
        Question question1 = Question.of("제목 이상하게하기", "내용삽입");
        question.update(question1, JAVAJIGI);
        softly.assertThat(question.getTitle()).isEqualTo(question1.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(question1.getContents());

    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문타인업데이트() {
        Question question = newQuestion();
        Question question1 = Question.of("제목 이상하게하기", "내용삽입");
        question.update(question1, SANJIGI);
        softly.assertThat(question.getTitle()).isEqualTo(question1.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(question1.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문손님업데이트() {
        Question question = newQuestion();
        Question question1 = Question.of("제목 이상하게하기", "내용삽입");
        question.update(question1, User.GUEST_USER);
        softly.assertThat(question.getTitle()).isEqualTo(question1.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(question1.getContents());
    }

    @Test
    public void 삭제하기() {
        Question question = newQuestion();
        question.delete(JAVAJIGI);
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 삭제하기_손님() {
        Question question = newQuestion();
        question.delete(User.GUEST_USER);
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 삭제하기_타인() {
        Question question = newQuestion();
        question.delete(SANJIGI);
        softly.assertThat(question.isDeleted()).isTrue();
    }


    //답변 CRUD

    @Test
    public void 답변하기() {
        Question question = newQuestion();
        Answer answer = Answer.of(JAVAJIGI, "답변하기 테스트.");
        question.addAnswer(answer);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변하기_손님() {
        Question question = newQuestion();
        Answer answer = Answer.of(User.GUEST_USER, "답변하기 테스트.");
        question.addAnswer(answer);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변하기_로그아웃() {
        Question question = newQuestion();
        Answer answer = Answer.of(null, "답변하기 테스트.");
        question.addAnswer(answer);
    }


    @Test
    public void 답변_수정() {
        Question question = newQuestion();
        Answer answer = Answer.of(JAVAJIGI, "답변하기 테스트.");
        question.addAnswer(answer);
        Answer updateAnswer = Answer.of(JAVAJIGI, "다른내용으로 수정");
        answer.update(updateAnswer);
        softly.assertThat(answer.getContents()).isEqualTo(updateAnswer.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변_타인수정() {
        Question question = newQuestion();
        Answer answer = Answer.of(JAVAJIGI, "답변하기 테스트.");
        question.addAnswer(answer);
        Answer updateAnswer = Answer.of(SANJIGI, "다른내용으로 수정");
        answer.update(updateAnswer);
    }

    @Test
    public void 답변_삭제() {
        Question question = newQuestion();
        Answer answer = Answer.of(JAVAJIGI, "답변하기 테스트.");
        question.addAnswer(answer);
        answer.delete(JAVAJIGI);
        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변_타인삭제() {
        Question question = newQuestion();
        Answer answer = Answer.of(JAVAJIGI, "답변하기 테스트.");
        question.addAnswer(answer);
        answer.delete(SANJIGI);
        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변_손님삭제() {
        Question question = newQuestion();
        Answer answer = Answer.of(JAVAJIGI, "답변하기 테스트.");
        question.addAnswer(answer);
        answer.delete(User.GUEST_USER);
        softly.assertThat(answer.isDeleted()).isTrue();
    }

}