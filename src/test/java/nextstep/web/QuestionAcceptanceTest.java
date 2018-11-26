package nextstep.web;

import nextstep.util.HtmlFormDataBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static java.lang.String.format;

@SuppressWarnings({"NonAsciiCharacters", "SpellCheckingInspection"})
public class QuestionAcceptanceTest extends AcceptanceTest {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Test
    public void 로그인된_상태에서_질문_작성_화면으로_이동() {
        final ResponseEntity<String> response
                = basicAuthTemplate()
                .getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 로그인하지_않은_상태에서_질문_작성_화면으로_이동() {
        final ResponseEntity<String> response
                = template()
                .getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 로그인한_상태에서_질문_등록() {

        final HttpEntity<MultiValueMap<String, Object>> request
                = HtmlFormDataBuilder.urlEncodedForm()
                .post()
                .addParameter("title", "타이틀")
                .addParameter("contents", "컨텐츠")
                .build();

        final ResponseEntity<String> response
                = basicAuthTemplate()
                .postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void 로그인_하지_않은_상태에서_질문_등록() {

        final HttpEntity<MultiValueMap<String, Object>> request
                = HtmlFormDataBuilder.urlEncodedForm()
                .post()
                .addParameter("title", "타이틀")
                .addParameter("contents", "컨텐츠")
                .build();

        final ResponseEntity<String> response
                = template()
                .postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 로그인_상태에서_내가_등록한_질문_수정() {

        final long questionId = 1;
        final HttpEntity<MultiValueMap<String, Object>> request
                = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "타이틀")
                .addParameter("contents", "컨텐츠")
                .build();

        final ResponseEntity<String> response
                = basicAuthTemplate()
                .postForEntity(format("/questions/%d", questionId), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void 로그인_하지_않은_상태에서_글_수정_시도() {

        final long questionId = 1;
        final HttpEntity<MultiValueMap<String, Object>> request
                = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "타이틀")
                .addParameter("contents", "컨텐츠")
                .build();

        final ResponseEntity<String> response
                = template()
                .postForEntity(format("/questions/%d", questionId), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 로그인_하지_않은_상태에서_삭제_시도() {

        final long questionId = 1;
        final ResponseEntity<String> response
                = template()
                .exchange(format("/questions/%d", questionId), HttpMethod.DELETE, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 로그인_상태에서_내가_작성한_글_삭제() {

        final long questionId = 2;
        final ResponseEntity<String> response
                = basicAuthTemplate(findByUserId("sanjigi"))
                .exchange(format("/questions/%d", questionId), HttpMethod.DELETE, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void 로그인_상태에서_다른_사람의_작성한_글_삭제() {

        final long questionId = 1;
        final ResponseEntity<String> response
                = basicAuthTemplate(findByUserId("ninezero90hy"))
                .exchange(format("/questions/%d", questionId), HttpMethod.DELETE, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void 로그인_하지_않은_상태에서_질문_목록조회() {

        final ResponseEntity<String> response
                = template()
                .exchange("/questions", HttpMethod.GET, null, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
