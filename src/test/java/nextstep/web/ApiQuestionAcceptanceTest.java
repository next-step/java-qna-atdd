package nextstep.web;

import nextstep.domain.Question;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Test
    public void 생성() throws Exception {
        Question newQuestion = defaultQuestion();
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        Question dbQuestion = basicAuthTemplate().getForObject(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();
    }

    @Test
    public void 조회_상세() throws Exception {
        Question newQuestion = defaultQuestion();
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();
        Question original = basicAuthTemplate().getForObject(location, Question.class);

        Question question =
                basicAuthTemplate().getForObject(location, Question.class);
        softly.assertThat(original.equalsTitleAndContents(question)).isTrue();
    }

    @Test
    public void 수정() throws Exception {
        Question newQuestion = defaultQuestion();
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();
        Question original = basicAuthTemplate().getForObject(location, Question.class);

        Question updateQuestion = new Question(original.getTitle(), "수정된 내용");

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateQuestion.equalsTitleAndContents(responseEntity.getBody())).isTrue();
    }

    @Test
    public void 수정_비로그인_사용자() throws Exception {
        Question newQuestion = defaultQuestion();
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();
        Question original = basicAuthTemplate().getForObject(location, Question.class);

        Question updateQuestion = new Question(original.getTitle(), "수정된 내용");

        ResponseEntity<String> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 수정_다른_사용자() throws Exception {
        Question newQuestion = defaultQuestion();
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();
        Question original = basicAuthTemplate().getForObject(location, Question.class);

        Question updateQuestion = new Question(original.getTitle(), "수정된 내용");

        ResponseEntity<String> responseEntity =
                basicAuthTemplate(findByUserId("sanjigi")).exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 삭제() throws Exception {
        Question newQuestion = defaultQuestion();
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();
        Question original = basicAuthTemplate().getForObject(location, Question.class);

        ResponseEntity<Void> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.DELETE, createHttpEntity(original.getId()), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(basicAuthTemplate().getForObject(location, Question.class).isDeleted()).isTrue();
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
