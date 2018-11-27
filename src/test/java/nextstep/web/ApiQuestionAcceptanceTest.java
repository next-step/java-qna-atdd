package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionTest;
import nextstep.domain.User;
import nextstep.domain.UserTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
	public static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);
	
	@Test
	public void create() {
		User loginUser = defaultUser();
		Question question = QuestionTest.newQuestion(loginUser);
		ResponseEntity<Void> response = basicAuthTemplate(loginUser).postForEntity("/api/questions", question, Void.class);
		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		String location = response.getHeaders().getLocation().getPath();
		
		ResponseEntity<Question> dbQuestion = basicAuthTemplate(loginUser).getForEntity(location, Question.class);
		softly.assertThat(dbQuestion).isNotNull();
	}
}
