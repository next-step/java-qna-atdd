package nextstep.web;

import nextstep.domain.Question;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static nextstep.domain.QuestionTest.newQuestion;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private Question question;
    private String location;

    @Before
    public void setUp() throws Exception {
        question = newQuestion();
        location = createResource("/api/questions", question, defaultUser());
    }

    @Test
    public void 생성() throws Exception {
        softly.assertThat(getResource(location, Question.class)).isNotNull();
    }

    @Test
    public void 조회_상세() throws Exception {
        softly.assertThat(getResource(location, Question.class).equalsTitleAndContents(question)).isTrue();
    }

    @Test
    public void 수정() throws Exception {
        Question updateQuestion = new Question("수정된 제목", "수정된 내용");
        ResponseEntity<Question> responseEntity = basicAuthTemplate()
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateQuestion.equalsTitleAndContents(responseEntity.getBody())).isTrue();
    }

    @Test
    public void 수정_비로그인_사용자() throws Exception {
        Question updateQuestion = new Question("수정된 제목", "수정된 내용");
        ResponseEntity<String> responseEntity = template()
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 수정_다른_사용자() throws Exception {
        Question updateQuestion = new Question("수정된 제목", "수정된 내용");
        ResponseEntity<String> responseEntity = basicAuthTemplate(findByUserId("sanjigi"))
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 삭제() throws Exception {
        Question original = getResource(location, Question.class, defaultUser());
        ResponseEntity<Void> responseEntity = basicAuthTemplate()
                .exchange(location, HttpMethod.DELETE, createHttpEntity(original.getId()), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(basicAuthTemplate().getForObject(location, Question.class)).isNull();
    }

    @Test
    public void 삭제_다른_사용자() throws Exception {
        Question original = getResource(location, Question.class, defaultUser());
        ResponseEntity<Void> responseEntity = basicAuthTemplate(findByUserId("sanjigi"))
                .exchange(location, HttpMethod.DELETE, createHttpEntity(original.getId()), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
