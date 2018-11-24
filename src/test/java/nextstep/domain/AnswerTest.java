package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Created by hspark on 24/11/2018.
 */
public class AnswerTest {

	@Test(expected = UnAuthorizedException.class)
	public void test_다른사용자가_삭제() {
		User user = new User(1L, "test", "test", "test", "test");
		User user2 = new User(2L, "test2", "test", "test", "test");
		Answer answer = new Answer(user, "test");
		answer.delete(user2);
	}

	@Test
	public void test_삭제() {
		User user = new User(1L, "test", "test", "test", "test");
		Answer answer = new Answer(user, "test");
		answer.delete(user);
		Assertions.assertThat(answer.isDeleted()).isTrue();
	}
}