package nextstep.web;

import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import util.HtmlFormDataBuilder;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void createForm_login() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/questions/form", loginUser.getId()), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains("question");
        softly.assertThat(response.getBody()).contains("title");
        softly.assertThat(response.getBody()).contains("contents");
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create_login() {
        HttpEntity<MultiValueMap<String, Object>> request =
                HtmlFormDataBuilder.urlEncodedForm()
                        .addParameter("title", "testTitle")
                        .addParameter("contents", "testContents")
                        .build();

        ResponseEntity<String> response = template().postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() {
        HttpEntity<MultiValueMap<String, Object>> request =
                HtmlFormDataBuilder.urlEncodedForm()
                        .addParameter("title", "testTitle")
                        .addParameter("contents", "testContents")
                        .build();

        ResponseEntity<String> response = postWithLoginUser("/questions", request);


        log.debug("body : {}", response.getBody());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    private ResponseEntity<String> postWithLoginUser(String url, HttpEntity<MultiValueMap<String, Object>> request) {
        User loginUser = defaultUser();
        return basicAuthTemplate(loginUser)
                .postForEntity(url, request, String.class);
    }

    @Test
    public void list() {
        ResponseEntity<String> response = template().getForEntity("/questions", String.class);

        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(defaultQuestion().getTitle());
    }

    @Test
    public void detail_no_login() throws Exception {
        mockMvc.perform(get("/questions/" + defaultQuestion().getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("isModifiable"))
                .andExpect(model().attributeExists("question"))
                .andExpect(view().name("/qna/show"))
                .andExpect(content().string(containsString(defaultQuestion().getTitle())))
                .andExpect(content().string(containsString(defaultQuestion().getContents())));
    }

    @Test
    public void detail_login() throws Exception {
        mockMvc.perform(get("/questions/" + defaultQuestion().getId())
                .with(httpBasic(defaultUser().getUserId(), defaultUser().getPassword())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("isModifiable"))
                .andExpect(model().attributeExists("question"))
                .andExpect(view().name("/qna/show"))
                .andExpect(content().string(containsString(defaultQuestion().getTitle())))
                .andExpect(content().string(containsString(defaultQuestion().getContents())));
    }

    @Test
    public void updateForm_no_login() {
        ResponseEntity<String> response = template().getForEntity(
                                                        String.format("/questions/%d/form",
                                                        defaultQuestion().getId()),
                                                        String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void updateForm_login() {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).getForEntity(
                                                    String.format("/questions/%d/form", loginUser.getId()),
                                                    String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(defaultQuestion().getTitle());
        softly.assertThat(response.getBody()).contains(defaultQuestion().getContents());
    }

    @Test
    public void update_no_login() throws Exception {
        ResponseEntity<String> response = update(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void update_login() throws Exception {
        ResponseEntity<String> response = update(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions/" + defaultQuestion().getId());
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void delete_no_login() throws Exception {
        ResponseEntity<String> response = delete(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void delete_login() throws Exception {
        ResponseEntity<String> response = delete(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request =
                HtmlFormDataBuilder.urlEncodedForm()
                        .put()
                        .addParameter("title", "testTitle2")
                        .addParameter("contents", "testContents2")
                        .build();

        return template.postForEntity(String.format("/questions/%d", defaultQuestion().getId()), request, String.class);
    }

    private ResponseEntity<String> delete(TestRestTemplate template) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request =
                HtmlFormDataBuilder.urlEncodedForm()
                        .delete()
                        .build();

        return template.postForEntity(String.format("/questions/%d", defaultQuestion().getId()), request, String.class);
    }

}
