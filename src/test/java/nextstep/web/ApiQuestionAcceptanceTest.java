package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionBody;
import nextstep.domain.User;
import nextstep.dto.ListResponse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    @Test
    public void 질문_목록을_조회한다() {
        ListResponse<Question> result = getListResource(
            "/api/questions", Question.class, defaultUser()).getBody();

        softly.assertThat(result.getCount()).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void 질문_상세를_조회한다() {
        Question question = getResource(String.format("/api/questions/%d", 1), Question.class, defaultUser()).getBody();

        softly.assertThat(question.getQuestionBody()).isNotNull();
    }

    @Test
    public void 질문을_등록한다() {
        QuestionBody payload = new QuestionBody("This is title", "This is contents");
        ResponseEntity<Void> createResponse = createResource("/api/questions", payload, defaultUser());

        String location = createResponse.getHeaders().getLocation().getPath();

        Question createdQuestion = getResource(location, Question.class, defaultUser()).getBody();

        softly.assertThat(createdQuestion.getQuestionBody()).isEqualTo(payload);
    }

    @Test
    public void 질문을_수정한다() {
        QuestionBody payload = new QuestionBody("This is title", "This is contents");
        ResponseEntity<Void> createResponse = createResource("/api/questions", payload, defaultUser());

        String location = createResponse.getHeaders().getLocation().getPath();

        QuestionBody newPayload = new QuestionBody("This is updated title", "This is updated contents");
        Question updatedQuestion = updateResource(location, newPayload, Question.class, defaultUser()).getBody();

        softly.assertThat(updatedQuestion.getQuestionBody()).isEqualTo(newPayload);
    }

    @Test
    public void 질문을_삭제한다() {
        QuestionBody payload = new QuestionBody("This is title", "This is contents");
        ResponseEntity<Void> createResponse = createResource("/api/questions", payload, defaultUser());

        String location = createResponse.getHeaders().getLocation().getPath();
        deleteResource(location, defaultUser());

        ResponseEntity<Void> getResponse = basicAuthTemplate(defaultUser()).getForEntity(location, Void.class);

        softly.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /*
    @Test
    public void 로그인_하지않아도_조회는_가능하다() {
        ResponseEntity<ListResponse<Question>> listResponse = template().exchange("/api/questions",
            HttpMethod.GET, null, new ParameterizedTypeReference<ListResponse<Question>>() {});
        ResponseEntity<Question> detailResponse = getResource(String.format("/api/questions/%d", 1), Question.class, defaultUser());

        softly.assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(detailResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // check 제거할 수 있는 부분?
    @Test
    public void 로그인한_사용자가_아니면_질문을_등록할수_없다() {
        Question payload = new Question("This is title", "This is contents");
        ResponseEntity<Void> createResponse = template().postForEntity("/api/questions", payload, Void.class);

        softly.assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // check 제거할 수 있는 부분?
    @Test
    public void 작성자가_어니면_질문을_수정할수_없다() {
        Question payload = new Question("This is title", "This is contents");
        ResponseEntity<Void> createResponse = createResource("/api/questions", payload, defaultUser());

        String location = createResponse.getHeaders().getLocation().getPath();

        Question beUpdatedQuestion = new Question("This is updated title", "This is updated contents");
        ResponseEntity<Void> updateResponse = basicAuthTemplate(findByUserId("sanjigi")).exchange(location,
            HttpMethod.PUT,
            createHttpEntity(beUpdatedQuestion),
            Void.class);

        softly.assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    // check 제거할 수 있는 부분?
    @Test
    public void 작성자가_어니면_질문을_삭제할수_없다() {
        Question payload = new Question("This is title", "This is contents");
        ResponseEntity<Void> createResponse = createResource("/api/questions", payload, defaultUser());

        String location = createResponse.getHeaders().getLocation().getPath();
        ResponseEntity<Void> deleteResponse = basicAuthTemplate(findByUserId("sanjigi")).exchange(location,
            HttpMethod.DELETE,
            null,
            Void.class);

        softly.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
    */
}
