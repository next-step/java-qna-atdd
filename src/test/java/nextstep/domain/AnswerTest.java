package nextstep.domain;


import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

import java.util.Arrays;
import java.util.List;

public class AnswerTest extends BaseTest {
    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    public static final Question newQuestion() {
        return Question.ofUser("제목테스트", "내용테스트", JAVAJIGI);
    }

    public static Answer newAnswer() {
        return Answer.of(JAVAJIGI, "내용이 엉?");
    }

    public static Answer newAnswer(String contents) {
        return Answer.of(JAVAJIGI, contents);
    }
    //답변 CRUD
    String contents;
    @Before
    public void setUp() throws Exception {
        contents = "답변하기 콘텐츠";
    }

    @Test
    public void 답변하기() {
        Question question = newQuestion();
        Answer answer = Answer.of(JAVAJIGI, contents);
        question.addAnswer(answer);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변하기_손님() {
        Question question = newQuestion();
        Answer answer = Answer.of(User.GUEST_USER, contents);
        question.addAnswer(answer);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변하기_로그아웃() {
        Question question = newQuestion();
        Answer answer = Answer.of(null, contents);
        question.addAnswer(answer);
    }


    @Test
    public void 답변_수정() {
        Question question = newQuestion();
        Answer answer = Answer.of(JAVAJIGI, contents);
        question.addAnswer(answer);
        String contents = "다른내용으로 수정";
        answer.update(JAVAJIGI,contents);
        softly.assertThat(answer.getContents()).isEqualTo(contents);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변_타인수정() {
        Question question = newQuestion();
        Answer answer = Answer.of(JAVAJIGI, contents);
        question.addAnswer(answer);
        answer.update(SANJIGI,"다른내용으로 수정");
    }

    @Test
    public void 답변_삭제() throws CannotDeleteException {
        Question question = newQuestion();
        Answer answer = Answer.of(JAVAJIGI, contents);
        question.addAnswer(answer);
        answer.delete(JAVAJIGI);
        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void 답변_타인삭제() throws CannotDeleteException {
        Question question = newQuestion();
        Answer answer = Answer.of(JAVAJIGI, contents);
        question.addAnswer(answer);
        answer.delete(SANJIGI);
        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void 답변_손님삭제() throws CannotDeleteException {
        Question question = newQuestion();
        Answer answer = Answer.of(JAVAJIGI, contents);
        question.addAnswer(answer);
        answer.delete(User.GUEST_USER);
        softly.assertThat(answer.isDeleted()).isTrue();
    }
    @Test
    public void 답변목록() {
        Answer answer = Answer.of(JAVAJIGI, contents);
        List<Answer> answers = Arrays.asList(answer,answer,answer);
        Question question = Question.ofList("제목 이상하게하기", "내용삽입", JAVAJIGI, answers);
        question.addAnswer(answer);
        softly.assertThat(question.getAnswerSize()).isGreaterThan(3);
    }
}