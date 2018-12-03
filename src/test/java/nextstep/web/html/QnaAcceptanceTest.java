package nextstep.web.html;

import nextstep.domain.Question;
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
import support.test.WebAcceptanceTest;
import support.util.HtmlFormBuilder;

import static support.util.HtmlFormBuilder.builder;

public class QnaAcceptanceTest extends WebAcceptanceTest {
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
        // given
        String title = "제목입니다.";
        String contents = "본문입니다.";

        MultiValueMap<String, Object> params = builder()
                .add("title", title)
                .add("contents", contents)
                .build();

        // when
        HttpEntity request = createWebRequestEntity(params);
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions", request, String.class);

        // then
        softly.assertThat(getResponseLocationPath(response)).startsWith("/questions/");
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        Long questionId = getQuestionIdFromPath(response);
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
        String title = "제목입니다.";
        String contents = "본문입니다.";

        MultiValueMap<String, Object> params = HtmlFormBuilder.put()
                .add("title", title)
                .add("contents", contents)
                .build();

        HttpEntity request = createWebRequestEntity(params);

        String updateUrl = "/questions/" + question.getId();
        ResponseEntity<String> response = basicAuthTemplate().exchange(updateUrl, HttpMethod.PUT, request, String.class);

        // then
        String redirectPath = getResponseLocationPath(response);
        softly.assertThat(redirectPath).startsWith("/questions/");
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        Long questionId = getQuestionIdFromPath(response);
        softly.assertThat(questionRepository.findById(questionId).isPresent()).isTrue();
    }

    @Test
    public void deleteQuestion() throws Exception {
        // given
        Question question = insertTestQuestion("타이틀", "컨텐츠");

        // when
        HttpEntity request = createWebRequestEntity();
        ResponseEntity<String> response = basicAuthTemplate().exchange("/questions/" + question.getId(), HttpMethod.DELETE, request, String.class);

        // then
        softly.assertThat(getResponseLocationPath(response)).isEqualTo("/");
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        softly.assertThat(questionRepository.findById(question.getId()).get().isDeleted()).isTrue();
    }


    private Long getQuestionIdFromPath(ResponseEntity<String> response) {
        String redirectPath = getResponseLocationPath(response);
        String[] split = redirectPath.split("/");
        return Long.valueOf(split[split.length - 1]);
    }
}
