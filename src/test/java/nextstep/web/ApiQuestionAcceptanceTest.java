package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionTest;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

	public static final String API_QUESTIONS_PATH = "/api/questions";

	@Test
	public void create() {
		Question newQuestion = QuestionTest.newQuestion("질문 생성", "질문 내용 생성");

		ResponseEntity<Void> response = createResource(basicAuthTemplate(), API_QUESTIONS_PATH, newQuestion);
		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		String location = response.getHeaders().getLocation().getPath();
		Question savedQuestion = getResource(template(), location, Question.class);
		softly.assertThat(savedQuestion.equalsTitleAndContents(newQuestion)).isTrue();
	}

	@Test
	public void createByGuest() {
		Question newQuestion = QuestionTest.newQuestion("질문 생성", "질문 내용 생성");

		ResponseEntity<Void> response = createResource(template(), API_QUESTIONS_PATH, newQuestion);
		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void showQuestion() {
		String location = createNewQuestion();

		Question savedQuestion = getResource(template(), location, Question.class);
		softly.assertThat(savedQuestion).isNotNull();
	}

	@Test
	public void updateByOwner() {
		Question updateQuestion = QuestionTest.newQuestion("수정된 제목", "수정된 내용");
		ResponseEntity<Question> responseEntity = update(basicAuthTemplate(), updateQuestion);

		softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		softly.assertThat(updateQuestion.equalsTitleAndContents(responseEntity.getBody())).isTrue();
	}

	@Test
	public void updateByOtherUser() {
		Question updateQuestion = QuestionTest.newQuestion("수정된 제목", "수정된 내용");
		ResponseEntity<Question> responseEntity = update(basicAuthTemplate(findByUserId("sanjigi")), updateQuestion);

		softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void updateByGuest() {
		Question updateQuestion = QuestionTest.newQuestion("수정된 제목", "수정된 내용");
		ResponseEntity<Question> responseEntity = update(template(), updateQuestion);

		softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	private ResponseEntity<Question> update(TestRestTemplate template,  Question updateQuestion) {
		String location = createNewQuestion();
		return updateResource(template, location, updateQuestion, Question.class);
	}

	@Test
	public void deleteByOwner() {
		String location = createNewQuestion();

		ResponseEntity<Void> response = deleteResource(basicAuthTemplate(), location);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void deleteByOtherUser() {
		String location = createNewQuestion();

		ResponseEntity<Void> response = deleteResource(basicAuthTemplate(findByUserId("sanjigi")), location);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void deleteByGuest() {
		String location = createNewQuestion();

		ResponseEntity<Void> response = deleteResource(template(), location);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	private String createNewQuestion() {
		ResponseEntity<Void> response = createResource(basicAuthTemplate(), API_QUESTIONS_PATH,
				QuestionTest.newQuestion());
		return response.getHeaders().getLocation().getPath();
	}
}
