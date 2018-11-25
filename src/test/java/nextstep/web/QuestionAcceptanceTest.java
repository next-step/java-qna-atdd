package nextstep.web;

import nextstep.domain.AnswerRepository;
import nextstep.domain.QuestionRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    public void 질문_만들기_로그인_사용자(){
        String title = "자바 강의";
        String contents = "TDD 교육을 듣자";

        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                                                .addParameter("title", title)
                                                .addParameter("contents", contents)
                                                .build();

        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity("/questions", request , String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findByTitleAndDeletedFalse(title).isPresent()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void 질문_만들기_비로그인_사용자(){
        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                                                 .build();

        ResponseEntity<String> response = template().postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문_수정_로그인_사용자(){
        String title = "TDD 교육";
        String contents = "TDD 교육을 들으면 리팩토링 상승";

        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                                                .put()
                                                .addParameter("title", title)
                                                .addParameter("contents", contents)
                                                .build();

        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity("/questions/1", request , String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findByIdAndDeletedFalse(1L).get().getTitle()).isEqualTo(title);
        softly.assertThat(questionRepository.findByIdAndDeletedFalse(1L).get().getContents()).isEqualTo(contents);
    }

    @Test
    public void 질문_수정_비로그인_사용자(){
        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                                                .put()
                                                .build();

        ResponseEntity<String> response = template().postForEntity("/questions/1", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문_수정_다른_로그인_사용자(){
        String title = "TDD 교육";
        String contents = "TDD 교육을 들으면 리팩토링 상승";

        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                                                .put()
                                                .addParameter("title", title)
                                                .addParameter("contents", contents)
                                                .build();

        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity("/questions/2", request , String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 질문_삭제_로그인_사용자(){
        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                                                .delete()
                                                .build();

        basicAuthTemplate(defaultUser()).postForEntity("/questions/3", request , String.class);

        softly.assertThat(questionRepository.findByIdAndDeletedFalse(3L).isPresent()).isFalse();
    }

    @Test
    public void 질문_삭제_비로그인_사용자(){
        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                                                .delete()
                                                .build();

        ResponseEntity<String> response = template().postForEntity("/questions/2", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문_삭제_다른_로그인_사용자(){

        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                                                .delete()
                                                .build();

        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity("/questions/2", request , String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 답변_작성_로그인_사용자(){
        String contents = "TDD 답변입니다.";

        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                            .addParameter("contents", contents)
                            .build();

        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity("/questions/1/answer", request , String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(answerRepository.findAllByQuestionId(1L)).extracting("contents").containsAnyOf(contents);
    }

    @Test
    public void 답변_작성_비로그인_사용자(){
        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                                                .build();

        ResponseEntity<String> response = template().postForEntity("/questions/1/answer", request , String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 답변_삭제_로그인_사용자(){

        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                                                .delete()
                                                .build();

        basicAuthTemplate(defaultUser())
                .postForEntity("/questions/1/answer/1", request , String.class);

        softly.assertThat(answerRepository.findByIdAndDeletedFalse(1L).isPresent()).isFalse();
    }

    @Test
    public void 답변_삭제_비로그인_사용자(){

        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                                                .delete()
                                                .build();

        ResponseEntity<String> response = template().postForEntity("/questions/1/answer/1", request , String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 답변_삭제_다른_로그인_사용자(){

        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                                                .delete()
                                                .build();

        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity("/questions/1/answer/2", request , String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }


}
