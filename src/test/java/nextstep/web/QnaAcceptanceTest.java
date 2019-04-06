package nextstep.web;

import nextstep.domain.Fixture;
import nextstep.domain.QuestionRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.aspect.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import static nextstep.domain.Fixture.mockUser;

public class QnaAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QnaAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void 질문_리스트_페이지() {
        ResponseEntity<String> response = basicAuthTemplate(mockUser).getForEntity("/questions", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void 질문_상세_페이지() {
        ResponseEntity<String> response = template().getForEntity("/questions/{id}", String.class, 1);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(questionRepository.findById(1L).isPresent()).isTrue();
        softly.assertThat(response.getBody()).contains("국내에서");
    }
    @Test
    public void 질문생성_페이지() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", Fixture.title)
                .addParameter("contents", Fixture.contents).build();

        ResponseEntity<String> response = basicAuthTemplate(mockUser).getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains("질문하기");
    }

    @Test
    public void 질문생성() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", Fixture.title)
                .addParameter("contents", Fixture.contents).build();

        ResponseEntity<String> response = basicAuthTemplate(mockUser).postForEntity("/questions", request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findById(3L).isPresent()).isTrue();
    }

    @Test
    public void 질문수정_페이지_이동() {
        ResponseEntity<String> response = basicAuthTemplate(mockUser).getForEntity("/questions/{id}/form", String.class, 1);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains("Ruby on Rails");
    }

    @Test
    public void 질문삭제() {
        ResponseEntity<String> response = basicAuthTemplate(mockUser)
                .exchange(String.format("/questions/%d", 2), HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    // TODO : 답변 CRUD 테스트 추가할 것!
}