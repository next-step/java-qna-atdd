package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import java.util.List;

import static org.assertj.core.api.Assertions.tuple;

public class QuestionTest extends BaseTest {

    @Test
    public void createQuestionTest() {
        // given
        String title = "타이틀";
        String contents = "컨텐츠";

        // when
        Question question = new Question(title, contents, basicUser);

        // then
        softly.assertThat(question.getTitle()).isEqualTo(title);
        softly.assertThat(question.getContents()).isEqualTo(contents);
        softly.assertThat(question.getWriter()).isEqualTo(basicUser);
    }

    @Test
    public void updateQuestion() {
        // given
        Question question = new Question("타이틀", "컨텐츠", basicUser);
        String newTitle = "수정된 타이틀";
        String newContents = "수정된 컨텐츠";

        // when
        question.update(basicUser, new Question(newTitle, newContents));

        //then
        softly.assertThat(question.getTitle()).isEqualTo(newTitle);
        softly.assertThat(question.getContents()).isEqualTo(newContents);
        softly.assertThat(question.getWriter()).isEqualTo(basicUser);
    }


    @Test(expected = UnAuthorizedException.class)
    public void updateQuestionWithInvalidUser() {
        // given
        Question question = new Question("타이틀", "컨텐츠", basicUser);

        // when
        question.update(anotherUser, new Question(" ", " "));
    }

    @Test
    public void deleteQuest() {
        // given
        Question question = new Question("타이틀", "컨텐츠", basicUser);

        // when
        question.delete(basicUser);

        //then
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = IllegalStateException.class)
    public void deleteQuestion_이미삭제된경우() {
        // given
        Question question = new Question("타이틀", "컨텐츠", basicUser);

        //when
        question.delete(basicUser);
        question.delete(basicUser);
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuest_유저가다른경우() {
        // given
        Question question = new Question("타이틀", "컨텐츠", basicUser);

        // when
        question.delete(anotherUser);
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteQuestion_다른사람답변존재시_삭제불가능() {
        // given
        Question question = new Question("타이틀", "컨텐츠", basicUser);
        question.addAnswer(new Answer(anotherUser, "contents"));

        //when
        question.delete(basicUser);
    }

    @Test
    public void deleteQuestion_자문자답시_삭제가능() {
        // given
        Question question = new Question("타이틀", "컨텐츠", basicUser);
        question.addAnswer(new Answer(basicUser, "contents"));

        //when
        question.delete(basicUser);
        //then
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test
    public void deleteQuestion_하위답변삭제처리() {
        // given
        Answer answer1 = new Answer(basicUser, "aaa");
        Answer answer2 = new Answer(basicUser, "bbb");

        Question question = new Question("타이틀", "컨텐츠", basicUser);
        question.addAnswer(answer1);
        question.addAnswer(answer2);

        softly.assertThat(answer1.isDeleted()).isFalse();
        softly.assertThat(answer2.isDeleted()).isFalse();

        //when
        question.delete(basicUser);

        //then
        softly.assertThat(answer1.isDeleted()).isTrue();
        softly.assertThat(answer2.isDeleted()).isTrue();
    }


    @Test
    public void deleteQuestion_히스토리생성확인() {
        // given
        Question question = new Question("타이틀", "questionContents", basicUser);
        Answer answer = new Answer(basicUser, "answerContents");

        question.addAnswer(answer);

        //when
        List<DeleteHistory> deleteHistories = question.delete(basicUser);

        //then
        softly.assertThat(deleteHistories).hasSize(2)
                .extracting("contentType", "deletedBy")
                .contains(
                        tuple(ContentType.QUESTION, basicUser),
                        tuple(ContentType.ANSWER, basicUser)
                );
    }

}
