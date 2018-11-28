package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Created by hspark on 23/11/2018.
 */
public class QuestionTest {
	@Test
	public void test_생성() {
		Question question = new Question("test", "테스트중입니다.");
		Assertions.assertThat(question.getTitle()).isEqualTo("test");
		Assertions.assertThat(question.getContents()).isEqualTo("테스트중입니다.");
		Assertions.assertThat(question.isDeleted()).isFalse();
	}

	@Test
	public void test_작성자지정() {
		Question question = new Question("test", "테스트중입니다.");
		User user = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
		question.writeBy(user);
		Assertions.assertThat(question.getWriter()).isEqualTo(user);
		Assertions.assertThat(question.isOwner(user)).isTrue();
	}

	@Test(expected = UnAuthorizedException.class)
	public void test_다른사용자_삭제() throws CannotDeleteException {
		Question question = new Question("test", "테스트중입니다.");
		User user = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
		User user2 = new User(2L, "javajigid", "password", "name", "javajigi@slipp.net");
		question.writeBy(user);
		question.delete(user2);
	}

	@Test
	public void test_삭제_답변없음() throws CannotDeleteException {
		Question question = new Question("test", "테스트중입니다.");
		User user = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
		question.writeBy(user);
		question.delete(user);
		Assertions.assertThat(question.isDeleted()).isTrue();
	}

	@Test
	public void test_삭제_답변도전부_작성자() throws CannotDeleteException {
		Question question = new Question("test", "테스트중입니다.");
		User user = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
		question.writeBy(user);
		Answer answer1 = new Answer(user, "1");
		Answer answer2 = new Answer(user, "2");

		question.addAnswer(answer1);
		question.addAnswer(answer2);

		question.delete(user);

		Assertions.assertThat(question.isDeleted()).isTrue();
		Assertions.assertThat(answer1.isDeleted()).isTrue();
		Assertions.assertThat(answer2.isDeleted()).isTrue();
	}

	@Test(expected = CannotDeleteException.class)
	public void test_삭제_답변중_다른사용자_존재() throws CannotDeleteException {
		Question question = new Question("test", "테스트중입니다.");
		User user = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
		User other = new User("sanjigi", "password", "name2", "javajigi@slipp.net2");
		question.writeBy(user);
		Answer answer1 = new Answer(user, "1");
		Answer answer2 = new Answer(other, "2");

		question.addAnswer(answer1);
		question.addAnswer(answer2);

		question.delete(user);

		Assertions.assertThat(question.isDeleted()).isFalse();
		Assertions.assertThat(answer1.isDeleted()).isFalse();
		Assertions.assertThat(answer2.isDeleted()).isFalse();
	}
}