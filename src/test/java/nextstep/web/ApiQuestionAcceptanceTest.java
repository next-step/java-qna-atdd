package nextstep.web;

import nextstep.domain.Question;
import nextstep.dto.QuestionDTO;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;
import support.test.RestApiExecutor;
import support.test.RestApiResult;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Test
    public void test_생성() throws Exception {
        String location = postTest();

        Question dbQuestion = basicAuthTemplate().getForObject(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();
    }

    @Test
    public void test_전체_조회() throws Exception {
        ResponseEntity<Void> response = basicAuthTemplate().getForEntity("/api/questions", Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void test_수정() throws Exception {
        String location = postTest();

        QuestionDTO updateQuestion = new QuestionDTO("테스트 제목 수정", "테스트 내용 수정");
        RestApiResult<Question> questionPutResponse = RestApiExecutor.ready(basicAuthTemplate(), Question.class)
                .put().url(location).request(updateQuestion).execute();

        softly.assertThat(questionPutResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(questionPutResponse.getBody().equalsContents(updateQuestion.getContents())).isTrue();
        softly.assertThat(questionPutResponse.getBody().equalsTitle(updateQuestion.getTitle())).isTrue();
    }

    @Test
    public void test_수정_다른사용자() throws Exception {

        String location = postTest();

        QuestionDTO updateQuestion = new QuestionDTO("테스트 제목 수정", "테스트 내용 수정");
        RestApiResult<Question> putResult = RestApiExecutor.ready(basicAuthTemplate(testUser()), Question.class)
                .put().url(location).request(updateQuestion).execute();

        softly.assertThat(putResult.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void test_삭제() throws Exception {
        String location = postTest();

        RestApiResult<Void> deleteResult = RestApiExecutor.ready(basicAuthTemplate(), Void.class).delete().url(location).execute();
        softly.assertThat(deleteResult.getStatusCode()).isEqualTo(HttpStatus.OK);

        Question dbQuestion = basicAuthTemplate().getForObject(location, Question.class);
        softly.assertThat(dbQuestion).isNull();
    }

    @Test
    public void test_삭제_다른사용자() throws Exception {
        String location = postTest();

        RestApiResult<Void> deleteResult = RestApiExecutor.ready(basicAuthTemplate(testUser()), Void.class).delete().url(location).execute();

        softly.assertThat(deleteResult.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        Question dbQuestion = basicAuthTemplate().getForObject(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();
    }

    private String postTest() {
        QuestionDTO question = new QuestionDTO("테스트 제목", "테스트 내용");
        RestApiResult<Void> result = RestApiExecutor.ready(basicAuthTemplate(), Void.class)
                .post().url("/api/questions").request(question).execute();
        softly.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return result.getResourceLocation();
    }
}
