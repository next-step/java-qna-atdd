package nextstep.web;

import nextstep.domain.AnswerRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.domain.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import java.util.Objects;

public class AnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Autowired
    private AnswerRepository answerRepository;


    //답변에 대한 폼 생성 - 로그인유저
    @Test
    public void createForm_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/answers/form", defaultQuestion().getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    //답변에 대한 폼 생성 - 로그인 안한 유저

    //답변에 대한 수정 - 로그인 유저(답변유저 아님)

    //답변에 대한 수정 - 로그인 안한 유저

    //답변에 대한 수정 - 로그인 유저(답변유저 맞음)


    private ResponseEntity<String> create(TestRestTemplate template) throws Exception {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();

        htmlFormDataBuilder.addParameter("contents", "답변드립니다.");
        HttpEntity request = htmlFormDataBuilder.build();
        return template.postForEntity("/questions/1/answers", request, String.class);

    }

    //답변 생성 - 로그인 유저
    @Test
    public void createAnswer_withLogin() throws Exception {
        ResponseEntity<String> response = create(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(answerRepository.findById(1L).isPresent()).isTrue();
        softly.assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).getPath()).startsWith("/questions");
    }

    //답변 생성 - 로그인 안한 유저
    @Test
    public void createAnswer_noLogin() throws Exception {
        ResponseEntity<String> response = create(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());

    }

    //답변 삭제 - 로그인 안한 유저
    @Test
    public void deleteAnswer_withLogin() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.urlEncodedForm().build();
        //ResponseEntity<String> response = basicAuthTemplate(defaultUser()).(String.format("/questions/answers/%d",defaultUser().getId()),request,String.class);
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).exchange(String.format("/questions/answers/%d", 1L), HttpMethod.DELETE, request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    //답변 삭제 - 로그인 유저(답변유저 맞음)
    @Test
    public void deleteAnswer_noLogin() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.urlEncodedForm().build();
        //ResponseEntity<String> response = basicAuthTemplate(defaultUser()).(String.format("/questions/answers/%d",defaultUser().getId()),request,String.class);
        ResponseEntity<String> response = template().exchange(String.format("/questions/answers/%d", 1L), HttpMethod.DELETE, request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

}
