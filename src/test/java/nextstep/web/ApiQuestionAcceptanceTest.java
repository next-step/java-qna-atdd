package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;


public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);


    //create
    @Test
    public void create_noLogin() throws Exception {
        Question newQuestion = new Question("새로운질문", "이것은 무엇입니까?");
        ResponseEntity<Void> response = template().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void create_withLogin() throws Exception {
        Question newQuestion = new Question("새로운질문", "이것은 무엇입니까?");
        ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    //show
    @Test
    public void show_list() throws Exception {
        Question newQuestion = new Question("새로운질문", "이것은 무엇입니까?");

        String location = createResource("/api/questions", newQuestion);

        ResponseEntity<Void> response = template().getForEntity(location, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    //update
    @Test
    public void update_withLogin() throws Exception {
        Question newQuestion = new Question("업데이트질문", "업데이트해주세요");
        String location = createResource("/api/questions", newQuestion);

        ResponseEntity<Question> responseEntity = basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.PUT, createHttpEntity(newQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(newQuestion.isOwner(defaultUser())).isTrue();

    }

    //update_다른사람
    @Test
    public void update_다른사람() throws Exception {
        Question newQuestion = new Question("업데이트질문2", "업데이트해주세요2");
        String location = createResource("/api/questions", newQuestion);

        ResponseEntity<Question> responseEntity = basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.PUT, createHttpEntity(newQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(newQuestion.isOwner(defaultUser())).isTrue();

    }

    //update_낫로그인
    @Test
    public void update_noLogin() throws Exception {
        Question newQuestion = new Question("업데이트질문3", "업데이트해주세요3");
        String location = createResource("/api/questions", newQuestion);

        ResponseEntity<String> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(newQuestion), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());

    }

    //delete
    @Test
    public void delete_withLogin() throws Exception {
        ;

        basicAuthTemplate(secondUser()).delete("/api/questions/2");
        softly.assertThat(questionRepository.findById(2L).get().isDeleted()).isTrue();
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}