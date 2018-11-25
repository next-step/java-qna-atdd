package nextstep.web;

import nextstep.domain.AnswerRepository;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import java.util.Arrays;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    public void 질문_만들기_로그인_사용자(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        User loginUser = defaultUser();
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        String title = "자바 강의";
        params.add("title", title);
        params.add("contents", "TDD 교육을 듣자");
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(params, headers);

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", request , String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findByTitleAndDeletedFalse(title).isPresent()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void 질문_만들기_비로그인_사용자(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        String title = "자바 강의";
        params.add("title", title);
        params.add("contents", "TDD 교육을 듣자");
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(params, headers);

        ResponseEntity<String> response = template().postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문_수정_로그인_사용자(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        User loginUser = defaultUser();
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("_method", HttpMethod.PUT.name());
        String title = "TDD 교육";
        params.add("title", title);
        String contents = "TDD 교육을 들으면 리팩토링 상승";
        params.add("contents", contents);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(params, headers);

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                    .postForEntity(String.format("/questions/1"), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(questionRepository.findByIdAndDeletedFalse(1L).get().getTitle()).isEqualTo(title);
        softly.assertThat(questionRepository.findByIdAndDeletedFalse(1L).get().getContents()).isEqualTo(contents);
    }

    @Test
    public void 질문_수정_비로그인_사용자(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("_method", HttpMethod.PUT.name());
        String title = "자바강의수정";
        params.add("title", title);
        String contents = "TDD 교육 내용을 수정합니다";
        params.add("contents", contents);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(params, headers);

        ResponseEntity<String> response = template().postForEntity("/questions/1", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문_삭제_로그인_사용자(){
        HttpHeaders headers = new HttpHeaders();

        User loginUser = defaultUser();
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("_method", HttpMethod.DELETE.name());
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(params, headers);

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions/2", request , String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(questionRepository.findByIdAndDeletedFalse(2L).isPresent()).isFalse();
    }

    @Test
    public void 질문_삭제_비로그인_사용자(){
        HttpHeaders headers = new HttpHeaders();

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("_method", HttpMethod.DELETE.name());
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(params, headers);

        ResponseEntity<String> response = template().postForEntity("/questions/2", request , String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 답변_작성_로그인_사용자(){
        HttpHeaders headers = new HttpHeaders();

        User loginUser = defaultUser();
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        String contents = "TDD 답변입니다.";
        params.add("contents", contents);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(params, headers);

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions/1/answer", request , String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(answerRepository.findAllByQuestionId(1L)).extracting("contents").containsAnyOf(contents);
    }

    @Test
    public void 답변_작성_비로그인_사용자(){
        HttpHeaders headers = new HttpHeaders();

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        String contents = "TDD 답변입니다.";
        params.add("contents", contents);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(params, headers);

        ResponseEntity<String> response = template().postForEntity("/questions/1/answer", request , String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }




}
