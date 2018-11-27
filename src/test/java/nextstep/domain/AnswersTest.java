package nextstep.domain;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class AnswersTest {

	private Answers answers;

	@Before
	public void setUp() throws Exception {
		answers = new Answers();
	}

	@Test
	public void answerIsEmpty() {
		assertThat(answers.isSameOwnerOfAllAnswer(UserTest.JAVAJIGI)).isTrue();
	}

	@Test
	public void hasSameOwnerOfAllAnswer() {
		Answer answer1 = AnswerTest.newAnswer(UserTest.JAVAJIGI, "답변1");
		Answer answer2 = AnswerTest.newAnswer(UserTest.JAVAJIGI, "답변2");
		answers.addAll(asList(answer1, answer2));

		assertThat(answers.isSameOwnerOfAllAnswer(UserTest.JAVAJIGI)).isTrue();
	}

	@Test
	public void hasOtherWriter() {
		Answer answer1 = AnswerTest.newAnswer(UserTest.SANJIGI, "답변1");
		Answer answer2 = AnswerTest.newAnswer(UserTest.JAVAJIGI, "답변2");
		answers.addAll(asList(answer1, answer2));

		assertThat(answers.isSameOwnerOfAllAnswer(UserTest.JAVAJIGI)).isFalse();
	}

}