package nextstep.web;

import nextstep.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static java.lang.String.format;
import static nextstep.domain.QuestionTest.newQuestion;

@SuppressWarnings({"NonAsciiCharacters", "SpellCheckingInspection"})
public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @SuppressWarnings("ConstantConditions")
    @Test
    public void 로그인한_상태에서_질문_생성() {

        final Question newQuestion = newQuestion("클린코드", "클린 코드란? 이런거에요.");
        final ResponseEntity<Void> response = createResource(newQuestion);

        final String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(location).isNotNull();

        final ResponseEntity<Question> dbQuestion = basicAuthTemplate().getForEntity(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();
    }

    @Test
    public void 로그인하지_않은_상태에서_질문_생성() {
        final Question newQuestion = newQuestion("클린코드2", "클린 코드란? 이런거에요.");
        final ResponseEntity<Void> response = template().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void 로그인_상태에서_내가_등록한_질문_수정() {

        final Question newQuestion = newQuestion("클린코드", "클린 코드란? 이런거에요.");
        final ResponseEntity<Void> response = createResource(newQuestion);

        final String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(location).isNotNull();

        final ResponseEntity<Question> dbQuestion = basicAuthTemplate().getForEntity(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();

        final Question updateQuestion = dbQuestion.getBody();
        updateQuestion.update(defaultUser(), new Question("클린 코드 개정판", "클린 코드란????? 이런거에요."));
        final ResponseEntity<Question> responseQuestion
                = basicAuthTemplate(defaultUser())
                .exchange(location, HttpMethod.PUT, createEntity(updateQuestion), Question.class);
        softly.assertThat(responseQuestion.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseQuestion.getBody().eqTitleAndContents(updateQuestion)).isTrue();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void 로그인_하지_않은_상태에서_질문_수정_시도() {

        final Question newQuestion = newQuestion("클린코드", "클린 코드란? 이런거에요.");
        final ResponseEntity<Void> response = createResource(newQuestion);

        final String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(location).isNotNull();

        final ResponseEntity<Question> dbQuestion = basicAuthTemplate().getForEntity(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();

        final Question updateQuestion = dbQuestion.getBody();
        updateQuestion.update(defaultUser(), new Question("클린 코드 개정판", "클린 코드란????? 이런거에요."));
        final ResponseEntity<Question> responseQuestion
                = template()
                .exchange(location, HttpMethod.PUT, createEntity(updateQuestion), Question.class);
        softly.assertThat(responseQuestion.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void 로그인_상태에서_내가_작성한_글_삭제() {

        final Question newQuestion = newQuestion("클린코드3", "클린 코드란? 이런거에요.");
        final ResponseEntity<Void> response = createResource(newQuestion);

        final String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(location).isNotNull();

        final ResponseEntity<Question> dbQuestion = basicAuthTemplate().getForEntity(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();

        final Question savedQuestion = dbQuestion.getBody();
        final ResponseEntity<String> responseQuestion
                = basicAuthTemplate()
                .exchange(format("/api/questions/%d", savedQuestion.getId()), HttpMethod.DELETE, null, String.class);
        softly.assertThat(responseQuestion.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void 로그인하지_않은_상태에서_내가_작성한_글_삭제() {

        final Question newQuestion = newQuestion("클린코드3", "클린 코드란? 이런거에요.");
        final ResponseEntity<Void> response = createResource(newQuestion);

        final String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(location).isNotNull();

        final ResponseEntity<Question> dbQuestion = basicAuthTemplate().getForEntity(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();

        final Question savedQuestion = dbQuestion.getBody();
        final ResponseEntity<String> responseQuestion
                = template()
                .exchange(format("/api/questions/%d", savedQuestion.getId()), HttpMethod.DELETE, null, String.class);
        softly.assertThat(responseQuestion.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 로그인_상태에서_다른_사람의_작성한_질문_삭제() {
        final long questionId = 3;
        final ResponseEntity<String> responseQuestion
                = basicAuthTemplate()
                .exchange(format("/api/questions/%d", questionId), HttpMethod.DELETE, null, String.class);
        softly.assertThat(responseQuestion.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private HttpEntity<Question> createEntity(final Question updateQuestion) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(updateQuestion, headers);
    }

    private ResponseEntity<Void> createResource(final Question question) {
        final ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", question, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        return response;
    }

}