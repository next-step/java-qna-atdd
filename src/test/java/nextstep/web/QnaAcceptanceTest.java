package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.dto.QuestionDTO;
import org.junit.After;
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

public class QnaAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    private String createLocation;
    private long questionId;
    private long answerId;

    @Before
    public void setUp() {
        User loginUser = defaultUser();
        String location = createResourceWithUser("/api/questions", new Question("question title", "question contents"), loginUser);
        QuestionDTO questionDTO = getResource(location, QuestionDTO.class, defaultUser());
        questionId = questionDTO.getId();
        createLocation = location;
        String answerLocation = createResourceWithUser(String.format("/api/questions/%d/answer/add", questionId), new Answer(defaultUser(), "answer test"), loginUser);
        questionDTO = getResource(location, QuestionDTO.class, defaultUser());
        answerId = questionDTO.getAnswers().get(0).getId();
    }

    @Test
    public void showQuestion() {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/show/%d", questionId), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void createQuestionForm_no_login() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void createQuestionForm_login() {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void newQuestion_save() {
        User loginUser = defaultUser();
        Question question = new Question("question title", "question contents");
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.addParameter("userId", loginUser.getUserId());
        builder.addParameter("name", loginUser.getName());
        builder.addParameter("email", loginUser.getEmail());
        builder.addParameter("title", question.getTitle());
        builder.addParameter("contents", question.getContents());

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/questions/register", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void updateQuestion() {
        User loginUser = defaultUser();
        Question question = new Question("question title", "question contents update");
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.addParameter("userId", loginUser.getUserId());
        builder.addParameter("name", loginUser.getName());
        builder.addParameter("email", loginUser.getEmail());
        builder.addParameter("title", question.getTitle());
        builder.addParameter("contents", question.getContents());

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(String.format("/questions/update/%d", questionId), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void deleteQuestion() {
        User loginUser = defaultUser();
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.delete();
        builder.addParameter("userId", loginUser.getUserId());
        builder.addParameter("name", loginUser.getName());
        builder.addParameter("email", loginUser.getEmail());

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        newQuestion_save();

        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(String.format("/questions/delete/%d", questionId), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void addAnswer() {
        User loginUser = defaultUser();
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.addParameter("userId", loginUser.getUserId());
        builder.addParameter("name", loginUser.getName());
        builder.addParameter("email", loginUser.getEmail());
        builder.addParameter("contents", "answer test");

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(String.format("/questions/%d/answer/add", questionId), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void deleteAnswer() {
        User loginUser = defaultUser();
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.delete();
        builder.addParameter("userId", loginUser.getUserId());
        builder.addParameter("name", loginUser.getName());
        builder.addParameter("email", loginUser.getEmail());

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(String.format("/questions/%d/answer/delete/%d", questionId, answerId), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @After
    public void tearDown() throws Exception {
        questionRepository.deleteAll();
    }
}