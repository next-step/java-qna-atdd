package nextstep.web;

import nextstep.domain.AnswerRepository;
import nextstep.domain.QuestionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

/**
 * Created by hspark on 23/11/2018.
 */

public class QnaAcceptanceTest extends AcceptanceTest {
	@Autowired
	private QuestionRepository questionRepository;
	@Autowired
	private AnswerRepository answerRepository;

	@Test
	public void test_질문생성() {
		TestRestTemplate template = basicAuthTemplate();
		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
			.post().addParameter("title", "테스트 제목").addParameter("contents", "테스트 내용").build();

		ResponseEntity<String> response = template.postForEntity("/questions", request, String.class);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
	}

	@Test
	public void test_로그인없이_질문생성() {
		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
			.post().addParameter("title", "테스트 제목").addParameter("contents", "테스트 내용").build();
		ResponseEntity<String> response = template().postForEntity("/questions", request, String.class);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void test_다른유저_질문수정() {
		TestRestTemplate template = basicAuthTemplate();

		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
			.put().addParameter("title", "테스트 제목").addParameter("contents", "테스트 내용").build();

		ResponseEntity<String> response = template.postForEntity(String.format("/questions/2"), request, String.class);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void test_질문수정() {
		TestRestTemplate template = basicAuthTemplate();

		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
			.put().addParameter("title", "테스트 제목")
			.addParameter("contents", "테스트 내용").build();

		ResponseEntity<String> response = template.postForEntity(String.format("/questions/1"), request, String.class);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		softly.assertThat(questionRepository.findById(1L).get().getTitle()).isEqualTo("테스트 제목");
		softly.assertThat(questionRepository.findById(1L).get().getContents()).isEqualTo("테스트 내용");
	}

	@Test
	public void test_질문삭제_다른사용자() {
		TestRestTemplate template = basicAuthTemplate();
		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().delete().build();
		ResponseEntity<String> response = template.postForEntity(String.format("/questions/2"), request, String.class);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		softly.assertThat(questionRepository.findById(2L).get().isDeleted()).isFalse();
	}

	@Test
	public void test_질문삭제_불가능() {
		TestRestTemplate template = basicAuthTemplate();

		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().delete().build();
		ResponseEntity<String> response = template.postForEntity(String.format("/questions/1"), request, String.class);

		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		softly.assertThat(questionRepository.findById(1L).get().isDeleted()).isFalse();
	}

	@Test
	public void test_답변생성() {
		TestRestTemplate template = basicAuthTemplate();
		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
			.post().addParameter("contents", "테스트 답변").build();
		ResponseEntity<String> response = template.postForEntity("/questions/1/answers", request, String.class);
		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		softly.assertThat(answerRepository.findAllByQuestionId(1)).extracting("contents").containsAnyOf("테스트 답변");
	}

	@Test
	public void test_답변삭제_다른사용자() {
		TestRestTemplate template = basicAuthTemplate();
		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().delete().build();
		ResponseEntity<String> response = template.postForEntity("/questions/1/answers/2", request, String.class);
		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void test_답변삭제() {
		TestRestTemplate template = basicAuthTemplate();
		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().delete().build();
		ResponseEntity<String> response = template.postForEntity("/questions/1/answers/1", request, String.class);
		softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		softly.assertThat(answerRepository.findById(1L).get().isDeleted()).isTrue();
	}

}
