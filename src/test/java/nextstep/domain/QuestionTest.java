package nextstep.domain;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.AlreadyDeletedException;
import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

public class QuestionTest {

	public static Question newQuestion() {
		return newQuestion("질문 제목", "질문 내용");
	}

	public static Question newQuestion(String title, String contents) {
		Question question = new Question(title, contents);
		question.writeBy(UserTest.JAVAJIGI);
		return question;
	}

	private Question question;

	@Before
	public void setUp() throws Exception {
		question = new Question("제목", "내용");
		question.writeBy(UserTest.JAVAJIGI);
	}

	@Test
	public void equalsTitleAndContents() {
		assertThat(question.equalsTitleAndContents(new Question("제목", "내용"))).isTrue();
	}

	@Test
	public void equalsTitleAndContentsWhenDifferentTitleOrContents() {
		assertThat(question.equalsTitleAndContents(new Question("제목1", "내용"))).isFalse();
		assertThat(question.equalsTitleAndContents(new Question("제목", "내용1"))).isFalse();
	}

	@Test
	public void update() {
		Question questionForUpdate = new Question("수정된 제목", "수정된 내용");

		question.update(question.getWriter(), questionForUpdate);

		assertThat(question.equalsTitleAndContents(questionForUpdate)).isTrue();
	}

	@Test(expected = UnAuthorizedException.class)
	public void updateByNoOwner() {
		question.update(UserTest.SANJIGI, new Question("수정된 제목", "수정된 내용"));
	}

	@Test
	public void delete() throws Exception {
		question.delete(UserTest.JAVAJIGI);

		assertThat(question.isDeleted()).isTrue();
	}

	@Test(expected = AlreadyDeletedException.class)
	public void deleteAlreadyDeleted() throws Exception {
		question.delete(UserTest.JAVAJIGI);
		question.delete(UserTest.JAVAJIGI);
	}

	@Test(expected = UnAuthorizedException.class)
	public void deleteFailByNoOwner() throws Exception {
		question.delete(UserTest.SANJIGI);
	}

	@Test
	public void deleteWhenAnswerIsEmpty() throws Exception {
		List<DeleteHistory> deleteHistories = question.delete(UserTest.JAVAJIGI);

		assertThat(deleteHistories).hasSize(1);
	}

	@Test
	public void deleteOnlyAnswerOfSameWriter() throws Exception {
		Answer answer1 = AnswerTest.newAnswer(UserTest.JAVAJIGI, "답변1");
		Answer answer2 = AnswerTest.newAnswer(UserTest.JAVAJIGI, "답변2");
		question.addAnswers(asList(answer1, answer2));

		List<DeleteHistory> deleteHistories = question.delete(UserTest.JAVAJIGI);

		assertThat(deleteHistories).hasSize(3);
	}

	@Test(expected = CannotDeleteException.class)
	public void deleteWhenHasAnswerOfOtherWriter() throws Exception {
		Answer answer1 = AnswerTest.newAnswer(UserTest.SANJIGI, "답변1");
		Answer answer2 = AnswerTest.newAnswer(UserTest.JAVAJIGI, "답변2");
		question.addAnswers(asList(answer1, answer2));

		question.delete(UserTest.JAVAJIGI);
	}
}