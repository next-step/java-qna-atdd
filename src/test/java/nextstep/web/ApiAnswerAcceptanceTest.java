package nextstep.web;

import nextstep.domain.Answer;
import nextstep.dto.AnswerDTO;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import support.test.AcceptanceTest;
import support.test.RestApiExecutor;
import support.test.RestApiResult;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

	@Test
	public void test_생성() throws Exception {
		AnswerDTO answerDTO = new AnswerDTO("테스트 내용");
		RestApiResult<Void> result = RestApiExecutor.ready(basicAuthTemplate(), Void.class)
			.post().url("/api/questions/1/answers").request(answerDTO).execute();
		softly.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	public void test_수정() throws Exception {
		AnswerDTO answerDTO = new AnswerDTO("테스트 내용");
		String location = getPostResource(answerDTO);

		AnswerDTO updateAnswerDTO = new AnswerDTO("테스트 내용 수정");
		RestApiResult<Answer> putResult = RestApiExecutor.ready(basicAuthTemplate(), Answer.class)
			.put().url(location).request(updateAnswerDTO).execute();

		softly.assertThat(putResult.getStatusCode()).isEqualTo(HttpStatus.OK);
		softly.assertThat(putResult.getBody().isEqualContents(updateAnswerDTO.getContents())).isTrue();
	}

	@Test
	public void test_수정_다른사용자() throws Exception {

		AnswerDTO answerDTO = new AnswerDTO("테스트 내용");
		String location = getPostResource(answerDTO);

		AnswerDTO updateAnswerDTO = new AnswerDTO("테스트 내용 수정");
		RestApiResult<Answer> putResult = RestApiExecutor.ready(basicAuthTemplate(testUser()), Answer.class)
			.put().url(location).request(updateAnswerDTO).execute();
		softly.assertThat(putResult.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void test_삭제() throws Exception {
		AnswerDTO answerDTO = new AnswerDTO("테스트 내용");
		String location = getPostResource(answerDTO);

		RestApiResult<Void> deleteResult = RestApiExecutor.ready(basicAuthTemplate(), Void.class).delete().url(location).execute();
		softly.assertThat(deleteResult.getStatusCode()).isEqualTo(HttpStatus.OK);

		Answer dbQuestion = basicAuthTemplate().getForObject(location, Answer.class);
		softly.assertThat(dbQuestion).isNull();
	}

	@Test
	public void test_삭제_다른사용자() throws Exception {
		AnswerDTO answerDTO = new AnswerDTO("테스트 내용");
		String location = getPostResource(answerDTO);

		RestApiResult<Void> deleteResult = RestApiExecutor.ready(basicAuthTemplate(testUser()), Void.class).delete().url(location).execute();
		softly.assertThat(deleteResult.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

		Answer dbQuestion = basicAuthTemplate().getForObject(location, Answer.class);
		softly.assertThat(dbQuestion).isNotNull();
	}

	private String getPostResource(AnswerDTO answerDTO) {
		RestApiResult<Void> postResult = RestApiExecutor.ready(basicAuthTemplate(), Void.class)
			.post().url("/api/questions/1/answers").request(answerDTO).execute();
		return postResult.getResourceLocation();
	}
}
