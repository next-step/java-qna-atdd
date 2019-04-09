package nextstep.web.api;

import lombok.extern.slf4j.Slf4j;
import nextstep.domain.*;
import nextstep.service.DeleteHistoryService;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static nextstep.domain.Fixture.MOCK_USER;
import static nextstep.domain.Fixture.OTHER_USER;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Slf4j
public class ApiQnaAcceptanceTest extends AcceptanceTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private DeleteHistoryService deleteHistoryService;

    @After
    public void tearDown() {
        answerRepository.deleteAll();
        questionRepository.deleteAll();
    }

    @Test
    public void 질문_생성() {
        String location = createResource("/api/questions", Fixture.MOCK_QUESTION);

        Question result = basicAuthTemplate().getForObject(location, Question.class);
        softly.assertThat(result).isNotNull();
        softly.assertThat(result.getTitle()).isEqualTo(Fixture.MOCK_QUESTION.getTitle());
        softly.assertThat(result.getContents()).isEqualTo(Fixture.MOCK_QUESTION.getContents());
    }

    @Test
    public void 질문_생성_실패_로그인안함() {
        ResponseEntity<String> response = template().postForEntity("/api/questions", Fixture.MOCK_QUESTION, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    public void 질문_수정() {
        String location = createResource("/api/questions", Fixture.MOCK_QUESTION);
        Question modifiedQuestion = Question.builder().title("바꾸자!").contents("으헣ㅎㅎㅎ").build();

        basicAuthTemplate().put(location, modifiedQuestion);

        Question result = basicAuthTemplate().getForObject(location, Question.class);
        softly.assertThat(result).isNotNull();
        softly.assertThat(result.getTitle()).isEqualTo("바꾸자!");
        softly.assertThat(result.getContents()).isEqualTo("으헣ㅎㅎㅎ");
    }

    @Test
    public void 질문_수정_실패_다른유저() {
        String location = createResource("/api/questions", Fixture.MOCK_QUESTION);
        Question modifiedQuestion = Question.builder().title("바꾸자!").contents("으헣ㅎㅎㅎ").build();

        ResponseEntity<Void> responseEntity = basicAuthTemplate(Fixture.OTHER_USER).exchange(location, HttpMethod.PUT, createHttpEntity(modifiedQuestion), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 없는_질문_수정_실패() {
        Question modifiedQuestion = Question.builder().title("바꾸자!").contents("으헣ㅎㅎㅎ").build();

        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange("/api/questions/7", HttpMethod.PUT, createHttpEntity(modifiedQuestion), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 답변0개이고_작성자와_삭제요청자가_같을때_성공() {
        String location = createResource("/api/questions", Fixture.MOCK_QUESTION);
        basicAuthTemplate().delete(location);

        Question result = basicAuthTemplate().getForObject(location, Question.class);
        softly.assertThat(result).isNotNull();
        softly.assertThat(result.isDeleted()).isTrue();

        DeleteHistory deleteResult = deleteHistoryService.findByContentId(result.getId());

        softly.assertThat(deleteResult.getContentType()).isEqualTo(ContentType.QUESTION);
        softly.assertThat(deleteResult.getContentId()).isEqualTo(result.getId());
        softly.assertThat(deleteResult.getDeletedBy()).isEqualTo(MOCK_USER);
    }

    @Test
    public void 질문_삭제_실패_답변있음() {
        String location = createResource("/api/questions", Fixture.MOCK_QUESTION);
        basicAuthTemplate(OTHER_USER).postForEntity(location + "/answers", "답변이애오 히히", String.class);
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.DELETE, createHttpEntity(null), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void 질문_삭제_실패_없는_질문_지우려고함() {
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange("/api/questions/7", HttpMethod.DELETE, createHttpEntity(null), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 답변_생성() {
        String questionLocation = createResource("/api/questions", Fixture.MOCK_QUESTION);
        String location = createResource(questionLocation + "/answers", "답변이애오 히히");

        Answer result = basicAuthTemplate().getForObject(location, Answer.class);
        softly.assertThat(result).isNotNull();
        softly.assertThat(result.getContents()).isEqualTo("답변이애오 히히");
    }

    @Test
    public void 답변_생성_실패_로그인안함() {
        String questionLocation = createResource("/api/questions", Fixture.MOCK_QUESTION);
        ResponseEntity<String> response = template().postForEntity(questionLocation + "/answers", Fixture.MOCK_QUESTION, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 답변_수정() {
        String questionLocation = createResource("/api/questions", Fixture.MOCK_QUESTION);
        String location = createResource(questionLocation + "/answers", "답변이애오 히히");

        Answer result = basicAuthTemplate().getForObject(location, Answer.class);
        softly.assertThat(result).isNotNull();
        softly.assertThat(result.getContents()).isEqualTo("답변이애오 히히");
    }

    @Test
    public void 답변_수정_실패_다른유저() {
        String questionLocation = createResource("/api/questions", Fixture.MOCK_QUESTION);
        String location = createResource(questionLocation + "/answers", "답변이애오 히히");

        ResponseEntity<Void> responseEntity = basicAuthTemplate(Fixture.OTHER_USER).exchange(location, HttpMethod.PUT, createHttpEntity("머리가 나빠 몸이 고생한다ㅠㅠ"), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 없는_답변_수정_실패() {
        String questionLocation = createResource("/api/questions", Fixture.MOCK_QUESTION);
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange(questionLocation + "/answers/9", HttpMethod.PUT, createHttpEntity("No"), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 답변_삭제() {
        String questionLocation = createResource("/api/questions", Fixture.MOCK_QUESTION);
        String location = createResource(questionLocation + "/answers", "답변이애오 히히");
        basicAuthTemplate().exchange(location, HttpMethod.DELETE, createHttpEntity(null), Void.class);

        Answer result = basicAuthTemplate().getForObject(location, Answer.class);
        softly.assertThat(result).isNotNull();
        softly.assertThat(result.isDeleted()).isTrue();

        DeleteHistory deleteResult = deleteHistoryService.findByContentId(result.getId());
        
        softly.assertThat(deleteResult.getContentType()).isEqualTo(ContentType.ANSWER);
        softly.assertThat(deleteResult.getContentId()).isEqualTo(result.getId());
        softly.assertThat(deleteResult.getDeletedBy()).isEqualTo(MOCK_USER);
    }

    @Test
    public void 없는_답변_삭제_실패() {
        String questionLocation = createResource("/api/questions", Fixture.MOCK_QUESTION);
        basicAuthTemplate().exchange(questionLocation + "/answers/9", HttpMethod.DELETE, createHttpEntity(null), Void.class);
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
