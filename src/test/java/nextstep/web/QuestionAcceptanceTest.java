package nextstep.web;

import nextstep.domain.AnswerRepository;
import nextstep.domain.ContentType;
import nextstep.domain.DeleteHistoryRepository;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class QuestionAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private DeleteHistoryRepository deleteHistoryRepository;

	@Test
	public void goCreateFormByLoginUser() {
		goCreateForm(basicAuthTemplate(), HttpStatus.OK);
	}

	@Test
	public void createFormByNoLogin() {
		goCreateForm(template(), HttpStatus.UNAUTHORIZED);
	}

	private void goCreateForm(TestRestTemplate template, HttpStatus httpStatus) {
		ResponseEntity<String> response = template.getForEntity("/questions/form", String.class);
		softly.assertThat(response.getStatusCode()).isEqualTo(httpStatus);
		log.debug("body : {}", response.getBody());
	}

	@Test
	public void createQuestion() {
		User loginUser = defaultUser();
		String title = "질문 제목", contents = "질문 내용 블라블라";
		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
				.addParameter("title", title)
				.addParameter("contents", contents)
				.build();
		ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/questions", request, String.class);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		softly.assertThat(response.getBody()).contains(title, contents);
	}

	@Test
	public void detailQuestion() {
		ResponseEntity<String> response = template().getForEntity("/questions/1", String.class);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void goUpdateFormByWriter() {
		User writer = defaultUser();
		ResponseEntity<String> response = goUpdateForm(basicAuthTemplate(writer), 1);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void goUpdateFormGuestUser() {
		ResponseEntity<String> response = goUpdateForm(template(), 1);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void goUpdateFormOtherUser() {
		ResponseEntity<String> response = goUpdateForm(basicAuthTemplate(findByUserId("sanjigi")), 1);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void goUpdateFormNotExistsQuestion() {
		User writer = defaultUser();
		ResponseEntity<String> response = goUpdateForm(basicAuthTemplate(writer), 99);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
	}

	private ResponseEntity<String> goUpdateForm(TestRestTemplate template, long questionId) {
		return template.getForEntity(String.format("/questions/%d/form", questionId), String.class);
	}

	@Test
	public void updateQuestionByWriter() {
		String title = "질문 제목 수정", contents = "질문 내용 수정";
		ResponseEntity<String> response = updateQuestion(basicAuthTemplate(), title, contents);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		softly.assertThat(response.getBody()).contains(title, contents);
	}

	@Test
	public void updateQuestionByOtherUser() {
		User loginUser = findByUserId("sanjigi");
		String title = "질문 제목 수정", contents = "질문 내용 수정";
		ResponseEntity<String> response = updateQuestion(basicAuthTemplate(loginUser), title, contents);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	private ResponseEntity<String> updateQuestion(TestRestTemplate template, String title, String contents) {
		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
				.put()
				.addParameter("title", title)
				.addParameter("contents", contents)
				.build();
		return template.postForEntity("/questions/1", request, String.class);
	}

	@Test
	public void deleteQuestionByOwner() {
		long questionId = 2;

		ResponseEntity<String> response = deleteQuestion(basicAuthTemplate(findByUserId("sanjigi")), questionId);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
		softly.assertThat(deleteHistoryRepository.countByContentTypeAndContentId(ContentType.QUESTION, questionId)).isEqualTo(1);
	}

	@Test
	public void deleteFailWhenHasAnswerOfOtherUser() {
		long questionId = 1;

		ResponseEntity<String> response = deleteQuestion(basicAuthTemplate(), questionId);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	private ResponseEntity<String> deleteQuestion(TestRestTemplate template, long questionId) {
		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
				.delete().build();

		return template.postForEntity("/questions/" + questionId, request, String.class);
	}

	@Test
	public void addAnswer() {
		User loginUser = findByUserId("sanjigi");
		long questionId = 2;
		String contents = "새로운 답변을 달다";
		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
				.addParameter("contents", contents)
				.build();
		ResponseEntity<String> response = basicAuthTemplate(loginUser)
				.postForEntity(String.format("/questions/%d/answers", questionId), request, String.class);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		softly.assertThat(answerRepository.findAllByQuestionId(questionId)).hasSize(1);
	}

	@Test
	public void deleteAnswer() {
		long answerId = 1;
		basicAuthTemplate().delete("/questions/2/answers/" + answerId, String.class);

		softly.assertThat(deleteHistoryRepository.countByContentTypeAndContentId(
				ContentType.ANSWER, answerId)).isEqualTo(1);
	}
}
