package nextstep.web;

import nextstep.builder.HtmlFormDataBuilder;
import nextstep.domain.AnswerRepository;
import nextstep.domain.QuestionBody;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import support.test.AcceptanceTest;


public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);
    private static final String QUESTIONS = "/questions";

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        String title = "제목 생성테스트";
        String contents = "내용 생성테스트";
        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", title)
                .addParameter("contents", contents)
                .build();
        QuestionBody questionBody = new QuestionBody(title,contents);

        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity(QUESTIONS, request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findByBodyAndDeletedFalse(questionBody).isPresent()).isTrue();
    }

    @Test
    public void list() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains(defaultQuestion().getTitle());
    }

    @Test
    public void updateForm_login() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .getForEntity(String.format("/questions/%d/form", defaultQuestion().getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(defaultQuestion().getContents());
    }

    @Test
    public void updateForm_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", defaultQuestion().getId()),
                String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<String> updateQuestion(TestRestTemplate template) throws Exception {
        String title = "제목 생성테스트";
        String contents = "내용 생성테스트";
        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                .addPutMethod()
                .addParameter("title", title)
                .addParameter("contents", contents)
                .build();
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity(QUESTIONS, request, String.class);

        return template.postForEntity(defaultQuestion().generateUrl(), request, String.class);
    }

    @Test
    public void updateQuestion() throws Exception {
        ResponseEntity<String> response = updateQuestion(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith(QUESTIONS);
    }


    @Test
    public void deleteQuestion() throws Exception {
        ResponseEntity<String> response = deleteQuestion(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<String> deleteQuestion(TestRestTemplate template) throws Exception {
        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                .addDeleteMethod()
                .build();
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity(QUESTIONS, request, String.class);
        return template.postForEntity(defaultQuestion().generateUrl(), request, String.class);
    }

    @Test
    public void showQuestion() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .getForEntity(defaultQuestion().generateUrl(), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(defaultQuestion().getContents());
    }

    //Answer CRUD

    @Test
    public void createAnswer() throws Exception {
        String contents = "내용 생성테스트";
        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("contents", contents)
                .build();
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity(String.format("/questions/%d/answers", defaultQuestion().getId()), request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith(QUESTIONS);
    }

    @Test
    public void createAnswer손님() throws Exception {
        String contents = "내용 생성테스트";
        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("contents", contents)
                .build();
        ResponseEntity<String> response = basicAuthTemplate(User.GUEST_USER)
                .postForEntity(String.format("/questions/%d/answers", defaultQuestion().getId()), request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    public void updateAnswer() throws Exception {
        String contents = "내용 생성테스트";
        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                .addPutMethod()
                .addParameter("contents", contents)
                .build();
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity(defaultAnswer().generateUrl(), request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith(QUESTIONS);
    }

    @Test
    public void updateAnswer타인() throws Exception {
        String contents = "내용 생성테스트";
        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                .addPutMethod()
                .addParameter("contents", contents)
                .build();
        ResponseEntity<String> response = basicAuthTemplate(otherUser())
                .postForEntity(defaultAnswer().generateUrl(), request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateAnswer손님() throws Exception {
        String contents = "내용 생성테스트";
        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                .addPutMethod()
                .addParameter("contents", contents)
                .build();
        ResponseEntity<String> response = basicAuthTemplate(User.GUEST_USER)
                .postForEntity(defaultAnswer().generateUrl(), request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    public void deleteAnswer() throws Exception {
        ResponseEntity<String> response = deleteAnswer(basicAuthTemplate(), defaultUser());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith(QUESTIONS);
    }

    @Test
    public void deleteAnswer타인() throws Exception {
        ResponseEntity<String> response = deleteAnswer(basicAuthTemplate(), otherUser());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void deleteAnswer손님() throws Exception {
        ResponseEntity<String> response = deleteAnswer(basicAuthTemplate(), User.GUEST_USER);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    private ResponseEntity<String> deleteAnswer(TestRestTemplate template, User user) throws Exception {
        HttpEntity request = HtmlFormDataBuilder.urlEncodedForm()
                .addDeleteMethod()
                .build();
        ResponseEntity<String> response = basicAuthTemplate(user)
                .postForEntity(QUESTIONS, request, String.class);
        return template.postForEntity(defaultAnswer().generateUrl(), request, String.class);
    }
}
