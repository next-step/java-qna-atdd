package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.AnswerRepository;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class AnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);
    private Answer javajigiAnswer;
    private Answer answer;

    @Autowired
    private AnswerRepository answerRepository;

    @Before
    public void setUp() throws Exception {
        javajigiAnswer = answerRepository.getOne(1L);
    }

    @Test
    public void create() {
        // given
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("contents", "답변이요.")
                .build();
        // when
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions/1/answers", request, String.class);
        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void delete_owner() {
        // given
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().delete().build();
        // when
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions/1/answers/1", request, String.class);
        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
        softly.assertThat(answerRepository.findById(1L).get().isDeleted()).isTrue();
    }

    @Test
    public void delete_not_owner() {
        // given
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().delete().build();
        // when
        ResponseEntity<String> response = basicAuthTemplate(findByUserId("sanjigi")).postForEntity("/questions/1/answers/1", request, String.class);
        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        softly.assertThat(answerRepository.findById(1L).get().isDeleted()).isFalse();
    }
}
