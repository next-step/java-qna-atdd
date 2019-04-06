package nextstep.web.api;

import nextstep.domain.Answer;
import nextstep.domain.Fixture;
import nextstep.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ApiQnaAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQnaAcceptanceTest.class);

    @Test
    public void 질문_생성() {
        String location = createResource("/api/questions", Fixture.MOCK_QUESTION);

        Question result = basicAuthTemplate().getForObject(location, Question.class);
        softly.assertThat(result).isNotNull();
        softly.assertThat(result.getTitle()).isEqualTo(Fixture.MOCK_QUESTION.getTitle());
        softly.assertThat(result.getContents()).isEqualTo(Fixture.MOCK_QUESTION.getContents());

    }

    //TODO : 질문생성 실패 언제함?

    @Test
    public void 질문_수정() {
        Question modifiedQuestion = Question.builder().title("바꾸자!").contents("으헣ㅎㅎㅎ").build();

        basicAuthTemplate().put("/api/questions/1", modifiedQuestion);

        Question result = basicAuthTemplate().getForObject("/api/questions/1", Question.class);
        softly.assertThat(result).isNotNull();
        softly.assertThat(result.getTitle()).isEqualTo("바꾸자!");
        softly.assertThat(result.getContents()).isEqualTo("으헣ㅎㅎㅎ");
    }

    @Test
    public void 질문_수정_실패() {
        Question modifiedQuestion = Question.builder().title("바꾸자!").contents("으헣ㅎㅎㅎ").build();

        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange("/api/questions/7", HttpMethod.PUT, createHttpEntity(modifiedQuestion), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 질문_삭제() {
        basicAuthTemplate().delete("/api/questions/1");

        Question result = basicAuthTemplate().getForObject("/api/questions/1", Question.class);
        softly.assertThat(result).isNotNull();
        softly.assertThat(result.isDeleted()).isTrue();
    }

    @Test
    public void 질문_삭제_실패() {
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange("/api/questions/7", HttpMethod.DELETE, createHttpEntity(null), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 답변_생성() {
        String location = createResource("/api/questions/1/answers", "답변이애오 히히");

        Answer result = basicAuthTemplate().getForObject(location, Answer.class);
        softly.assertThat(result).isNotNull();
        softly.assertThat(result.getContents()).isEqualTo("답변이애오 히히");
    }

    @Test
    public void 답변_수정() {
        basicAuthTemplate().put("/api/questions/1/answers/1", "답변이애오 히히");

        Answer result = basicAuthTemplate().getForObject("/api/questions/1/answers/1", Answer.class);
        softly.assertThat(result).isNotNull();
        softly.assertThat(result.getContents()).isEqualTo("답변이애오 히히");
    }

    @Test
    public void 답변_실패() {
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange("/api/questions/1/answers/9", HttpMethod.PUT, createHttpEntity("No"), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 답변_삭제() {
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange("/api/questions/1/answers/1", HttpMethod.DELETE, createHttpEntity(null), Void.class);

        Answer result = basicAuthTemplate().getForObject("/api/questions/1/answers/1", Answer.class);
        softly.assertThat(result).isNotNull();
        softly.assertThat(result.isDeleted()).isTrue();
    }

    @Test
    public void 답변_삭제_실패() {
        basicAuthTemplate().exchange("/api/questions/1/answers/9", HttpMethod.DELETE, createHttpEntity(null), Void.class);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }

    private String createResource(String path, Object bodyPayload) {
        ResponseEntity<String> response = basicAuthTemplate().postForEntity(path, bodyPayload, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getHeaders().getLocation().getPath();
    }
}
