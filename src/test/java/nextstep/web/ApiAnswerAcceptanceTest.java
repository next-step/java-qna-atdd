package nextstep.web;

import static nextstep.web.ApiQuestionAcceptanceTest.API_QUESTIONS_PATH;

import nextstep.domain.Answer;
import nextstep.domain.AnswerTest;
import nextstep.domain.QuestionTest;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

	@Test
	public void create() {
		String answerPrefixLocation = createNewQuestion();
		Answer newAnswer = AnswerTest.newAnswer("댓글 등록");

		ResponseEntity<Void> response = createResource(basicAuthTemplate(), answerPrefixLocation, newAnswer);
		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		String createdLocation = response.getHeaders().getLocation().getPath();
		Answer savedAnswer = getResource(template(), createdLocation, Answer.class);
		softly.assertThat(savedAnswer.equalsContents(savedAnswer)).isTrue();
	}

	@Test
	public void createByGuest() {
		String answerPrefixLocation = createNewQuestion();
		Answer newAnswer = AnswerTest.newAnswer("댓글 등록");

		ResponseEntity<Void> response = createResource(template(), answerPrefixLocation, newAnswer);
		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void show() {
		String location = createNewAnswer();

		Answer savedAnswer = getResource(template(), location, Answer.class);
		softly.assertThat(savedAnswer).isNotNull();
	}

	@Test
	public void deleteByOwner() {
		String location = createNewAnswer();

		ResponseEntity<Void> response = deleteResource(basicAuthTemplate(), location);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void deleteByOtherUser() {
		String location = createNewAnswer();

		ResponseEntity<Void> response = deleteResource(basicAuthTemplate(findByUserId("sanjigi")), location);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void deleteByGuest() {
		String location = createNewAnswer();

		ResponseEntity<Void> response = deleteResource(template(), location);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	private String createNewQuestion() {
		ResponseEntity<Void> response = createResource(basicAuthTemplate(), API_QUESTIONS_PATH,
				QuestionTest.newQuestion());
		return String.format("%s/answers" , response.getHeaders().getLocation().getPath());
	}

	private String createNewAnswer() {
		String answerPrefixLocation = createNewQuestion();
		ResponseEntity<Void> response = createResource(basicAuthTemplate(), answerPrefixLocation,
				AnswerTest.newAnswer());
		return response.getHeaders().getLocation().getPath();
	}
}
