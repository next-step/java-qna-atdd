package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.junit.Assert.*;

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
	public void test_다른사용자_삭제() {
		Question question = new Question("test", "테스트중입니다.");
		User user = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
		User user2 = new User(2L, "javajigid", "password", "name", "javajigi@slipp.net");
		question.writeBy(user);
		question.delete(user2);
	}

	@Test
	public void test_삭제() {
		Question question = new Question("test", "테스트중입니다.");
		User user = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
		question.writeBy(user);
		question.delete(user);
		Assertions.assertThat(question.isDeleted()).isTrue();
	}
}