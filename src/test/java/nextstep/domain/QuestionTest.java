package nextstep.domain;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

public class QuestionTest {

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

	@Test(expected = CannotDeleteException.class)
	public void deleteAlreadyDeleted() throws Exception {
		question.delete(UserTest.JAVAJIGI);
		question.delete(UserTest.JAVAJIGI);
	}

	@Test(expected = UnAuthorizedException.class)
	public void deleteFailByNoOwner() throws Exception {
		question.delete(UserTest.SANJIGI);
	}
}