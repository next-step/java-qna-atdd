package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    @Test
    public void 생성() throws Exception {
        Question question = defaultQuestion();
        Answer answer = new Answer(defaultUser(), "내용");
        String location = createResource("/api/questions/" + question.getId() + "/answers", answer, defaultUser());
        softly.assertThat(getResource(location, Question.class)).isNotNull();
    }

    @Test
    public void 수정() {
        Question question = defaultQuestion();
        Answer answer = new Answer(defaultUser(), "내용");
        String location = createResource("/api/questions/" + question.getId() + "/answers", answer, defaultUser());
        Answer updateAnswer = new Answer(defaultUser(), "수정된 내용");

        ResponseEntity<Answer> responseEntity = basicAuthTemplate()
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateAnswer), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(getResource(location, Answer.class, defaultUser()).getContents()).isEqualTo(updateAnswer.getContents());
    }

    @Test
    public void 삭제() {
        Question question = defaultQuestion();
        Answer answer = new Answer(defaultUser(), "내용");
        String location = createResource("/api/questions/" + question.getId() + "/answers", answer, defaultUser());
        Answer original = getResource(location, Answer.class, defaultUser());

        ResponseEntity<Void> responseEntity = basicAuthTemplate()
                .exchange(location, HttpMethod.DELETE, createHttpEntity(original.getId()), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(basicAuthTemplate().getForObject(location, Question.class)).isNull();
    }

    @Test
    public void 삭제_다른_사용자() {
        Question question = defaultQuestion();
        Answer answer = new Answer(defaultUser(), "내용");
        String location = createResource("/api/questions/" + question.getId() + "/answers", answer, defaultUser());
        Answer original = getResource(location, Answer.class, defaultUser());

        ResponseEntity<Void> responseEntity = basicAuthTemplate(findByUserId("sanjigi"))
                .exchange(location, HttpMethod.DELETE, createHttpEntity(original.getId()), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
