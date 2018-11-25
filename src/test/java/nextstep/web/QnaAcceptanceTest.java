package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import java.util.Arrays;

public class QnaAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QnaAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String title = "제목입니다.";
        String contents = "본문입니다.";

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("title", title);
        params.add("contents", contents);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(params, headers);
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions", request, String.class);

        String redirectPath = response.getHeaders().getLocation().getPath();
        softly.assertThat(redirectPath).startsWith("/questions/");
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        Long questionId = getQuestionIdFromPath(redirectPath);
        softly.assertThat(questionRepository.findById(questionId).isPresent()).isTrue();
    }

    @Test
    public void showQuestion() throws Exception {
        // given
        Question question = insertTestQuestion("질문 타이틀", "질문 컨텐츠");

        // when
        ResponseEntity<String> response = template().getForEntity("/questions/" + question.getId(), String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody())
                .contains(question.getTitle())
                .contains(question.getContents());
    }

    @Test
    public void modifyQuestion() throws Exception {
        // given
        Question question = insertTestQuestion("이전 타이틀", "이전 컨텐츠");

        // when
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String title = "제목입니다.";
        String contents = "본문입니다.";

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("title", title);
        params.add("contents", contents);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
        String updateUrl = "/questions/" + question.getId();
        ResponseEntity<String> response = basicAuthTemplate().exchange(updateUrl, HttpMethod.PUT, request, String.class);

        // then
        String redirectPath = response.getHeaders().getLocation().getPath();
        softly.assertThat(redirectPath).startsWith("/questions/");
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        Long questionId = getQuestionIdFromPath(redirectPath);
        softly.assertThat(questionRepository.findById(questionId).isPresent()).isTrue();
    }

    @Test
    public void deleteQuestion() throws Exception {
        // given
        Question question = insertTestQuestion("타이틀", "컨텐츠");

        // when
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));

        ResponseEntity<String> response = basicAuthTemplate().exchange("/questions/" + question.getId(), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);

        // then
        String redirectPath = response.getHeaders().getLocation().getPath();
        softly.assertThat(redirectPath).isEqualTo("/");
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        softly.assertThat(questionRepository.findById(question.getId()).get().isDeleted()).isTrue();
    }


    private Long getQuestionIdFromPath(String redirectPath) {
        String[] split = redirectPath.split("/");
        return Long.valueOf(split[split.length - 1]);
    }
}
