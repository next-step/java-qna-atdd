package nextstep.domain;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.AlreadyDeletedException;
import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

public class AnswerTest {

	public static Answer newAnswer() {
		return newAnswer("기본 답변 생성");
	}

	public static Answer newAnswer(String contents) {
		return newAnswer(UserTest.JAVAJIGI, contents);
	}

	public static Answer newAnswer(User writer, String contents) {
		return newAnswer(writer, QuestionTest.newQuestion(), contents);
	}

	public static Answer newAnswer(User writer, Question question) {
		return newAnswer(writer, question, "기본 답변 생성");
	}

	public static Answer newAnswer(User writer, Question question, String contents) {
		return new Answer(writer, question, contents);
	}

	private Answer answer;

	@Before
	public void setUp() throws Exception {
		answer = new Answer(UserTest.JAVAJIGI, "답변 내용");
	}

	@Test
	public void addAnswer() {
		Question question = new Question("제목", "내용");

		question.addAnswer(answer);

		assertThat(answer.getQuestion()).isEqualTo(question);
	}

	@Test
	public void delete() throws Exception {
		answer.delete(UserTest.JAVAJIGI);

		assertThat(answer.isDeleted()).isTrue();
	}

	@Test(expected = AlreadyDeletedException.class)
	public void deleteAlreadyDeleted() throws Exception {
		answer.delete(UserTest.JAVAJIGI);
		answer.delete(UserTest.JAVAJIGI);
	}

	@Test(expected = UnAuthorizedException.class)
	public void deleteFailByNoOwner() throws Exception {
		answer.delete(UserTest.SANJIGI);
	}
}