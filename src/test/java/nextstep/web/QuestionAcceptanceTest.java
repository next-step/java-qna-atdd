package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.web.lib.HtmlFormDataBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

public class QuestionAcceptanceTest extends AcceptanceTest {

    private static String ERORR_URL = "/questions/-1";

    @Autowired
    private QuestionRepository questionRepository;

    private static Question testQuestion;

    @Before
    public void setUp() throws Exception {
        Question question = new Question("title", "contents");
        question.writeBy(defaultUser());

        this.testQuestion = questionRepository.save(question);
    }

    @After
    public void tearDown() throws Exception {
        if (questionRepository.existsById(testQuestion.getId())) {
            questionRepository.deleteById(testQuestion.getId());
        }
    }

    @Test
    public void home_qnaList() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?");
    }

    @Test
    public void show() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", testQuestion.getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void show_wrongId() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/-1", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void create_form_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void create_form_login() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity("/questions/form", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void create_no_login() throws Exception {
        ResponseEntity<String> response = create(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void create_login() throws Exception {
        ResponseEntity<String> response = create(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    private ResponseEntity<String> create(TestRestTemplate template) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .post()
                .addParameter("title", "ATDD는 어떻게하면 좋을까요?")
                .addParameter("contents", "이렇게 하는게 맞는거겠죠?")
                .build();
        return template.postForEntity("/questions", request, String.class);
    }

    @Test
    public void delete_no_login() throws Exception {
        ResponseEntity<String> response = delete(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_login_owner() {
        ResponseEntity<String> response = delete(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    private ResponseEntity<String> delete(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();
        return template.postForEntity(String.format("/questions/%d", testQuestion.getId()), request, String.class);
    }

    @Test
    public void updateForm_no_login() {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", testQuestion.getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void updateForm_없는게시글() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity(ERORR_URL, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateForm_login() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity(String.format("/questions/%d/form", testQuestion.getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void updateForm_login_no_data() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity(ERORR_URL + "/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_no_login() {
        ResponseEntity<String> response = update(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_login() {
        ResponseEntity<String> response = update(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private ResponseEntity<String> update(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "제목수정")
                .addParameter("contents", "내용도 수정")
                .build();

        return template.postForEntity(String.format("/questions/%d", testQuestion.getId()), request, String.class);
    }
}
