package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import java.util.ArrayList;

public class QuestionTest extends BaseTest {

    public static Question newQuestion() {
        QuestionBody questionBody = dataultQuestionBody();
        return Question.ofList(questionBody, UserTest.JAVAJIGI, new ArrayList<Answer>());
    }

    public static Question newQuestion(String title, String contents) {
        QuestionBody questionBody = new QuestionBody(title, contents);
        return Question.ofList(questionBody, UserTest.JAVAJIGI, new ArrayList<Answer>());
    }

    public static QuestionBody dataultQuestionBody() {
        return new QuestionBody("지질하다", "내용이 엉?");
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
        QuestionBody updateQuestion = new QuestionBody("제목 이상하게하기", "내용삽입");
        question.update(UserTest.JAVAJIGI,updateQuestion);
        softly.assertThat(question.getTitle()).isEqualTo(updateQuestion.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(updateQuestion.getContents());

    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문타인업데이트() {
        Question question = newQuestion();
        QuestionBody updateQuestion = new QuestionBody("제목 이상하게하기", "내용삽입");
        question.update(UserTest.SANJIGI,updateQuestion);
        softly.assertThat(question.getTitle()).isEqualTo(updateQuestion.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(updateQuestion.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문손님업데이트() {
        Question question = newQuestion();
        QuestionBody updateQuestion = new QuestionBody("제목 이상하게하기", "내용삽입");
        question.update(User.GUEST_USER, updateQuestion);
        softly.assertThat(question.getTitle()).isEqualTo(updateQuestion.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(updateQuestion.getContents());
    }

    @Test
    public void 삭제하기() throws CannotDeleteException {
        Question question = newQuestion();
        question.delete(UserTest.JAVAJIGI);
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void 삭제하기타인() throws CannotDeleteException {
        Question question = newQuestion();
        question.delete(UserTest.SANJIGI);
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void 삭제하기손님() throws CannotDeleteException {
        Question question = newQuestion();
        question.delete(User.GUEST_USER);
        softly.assertThat(question.isDeleted()).isTrue();
    }


}