package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static java.lang.String.format;

@SuppressWarnings({"NonAsciiCharacters", "SpellCheckingInspection"})
public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    @Test
    public void 로그인하지_않은_상태에서_답변_등록() {

        final String contents = "좋은 책 소개 감사합니다.";
        final ResponseEntity<Void> response
                = template()
                .postForEntity("/api/questions/1/answers", contents, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 로그인한_상태에서_답변_등록() {
        final String contents = "좋은 책 소개 감사합니다.";
        final ResponseEntity<Void> response
                = basicAuthTemplate()
                .postForEntity("/api/questions/1/answers", contents, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void 로그인한_상태에서_등록한_답변_조회() {

        final String contents = "추천 좋아요^_^. :)";
        final String location = createResource(defaultUser(), contents);

        final ResponseEntity<Answer> dbAnswerResponse
                = basicAuthTemplate().getForEntity(location, Answer.class);
        softly.assertThat(dbAnswerResponse).isNotNull();
        softly.assertThat(dbAnswerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 로그인하지_않은_상태에서_등록된_답변_조회() {

        final String contents = "추천 좋아요!!. :-)";
        final String location = createResource(defaultUser(), contents);

        final ResponseEntity<Answer> dbAnswerResponse
                = template().getForEntity(location, Answer.class);
        softly.assertThat(dbAnswerResponse).isNotNull();
        softly.assertThat(dbAnswerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void 로그인한_상태에서_내가_등록한_답변_삭제() {

        final String contents = "혹시 클린코드 관련된 책 추천하시는거 있나요?";
        final String location = createResource(defaultUser(), contents);

        final ResponseEntity<Answer> dbAnswerResponse
                = basicAuthTemplate()
                .getForEntity(location, Answer.class);
        softly.assertThat(dbAnswerResponse).isNotNull();
        softly.assertThat(dbAnswerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        final Answer dbAnswer = dbAnswerResponse.getBody();
        final ResponseEntity<String> responseQuestion
                = basicAuthTemplate()
                .exchange(format("/api/questions/1/answers/%d", dbAnswer.getId()), HttpMethod.DELETE, null, String.class);
        softly.assertThat(responseQuestion.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void 로그인하지_않은_상태에서_답변_삭제() {

        final String contents = "책 내용 좋나요??";
        final String location = createResource(defaultUser(), contents);

        final ResponseEntity<Answer> dbAnswerResponse
                = basicAuthTemplate()
                .getForEntity(location, Answer.class);
        softly.assertThat(dbAnswerResponse).isNotNull();
        softly.assertThat(dbAnswerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        final Answer dbAnswer = dbAnswerResponse.getBody();
        final ResponseEntity<String> responseQuestion
                = template()
                .exchange(format("/api/questions/1/answers/%d", dbAnswer.getId()), HttpMethod.DELETE, null, String.class);
        softly.assertThat(responseQuestion.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void 로그인한_상태에서_다른_사람의_답변_삭제() {

        final String contents = "책 내용 좋나요?????????? 답글 부탁드립니다.";
        final String location = createResource(findByUserId("ninezero90hy"), contents);

        final ResponseEntity<Answer> dbAnswerResponse
                = basicAuthTemplate()
                .getForEntity(location, Answer.class);
        softly.assertThat(dbAnswerResponse).isNotNull();
        softly.assertThat(dbAnswerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        final Answer dbAnswer = dbAnswerResponse.getBody();
        final ResponseEntity<String> responseQuestion
                = basicAuthTemplate()
                .exchange(format("/api/questions/1/answers/%d", dbAnswer.getId()), HttpMethod.DELETE, null, String.class);
        softly.assertThat(responseQuestion.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private String createResource(final User user, final String contents) {
        final ResponseEntity<Void> response
                = basicAuthTemplate(user)
                .postForEntity("/api/questions/1/answers", contents, Void.class);

        final String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(location).isNotNull();
        return location;
    }

}
