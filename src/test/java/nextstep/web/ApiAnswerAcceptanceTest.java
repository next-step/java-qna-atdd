package nextstep.web;

import nextstep.domain.*;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    @Test
    public void answer_read_no_login() {
        // given
        Answer answer = answerOfDefaultUser();

        // when
        ResponseEntity<Answer> response = getAnswerResource(answer.generateUrl());

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        Answer dbAnswer = response.getBody();
        softly.assertThat(answer.getContents()).isEqualTo(dbAnswer.getContents());
    }

    @Test
    public void answer_create_no_login() {
        // given
        Question question = defaultQuestion();
        String contents = "Hello World";

        // when
        ResponseEntity<String> response = createAnswerResourceWithoutLogin(contents, question.getId());

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void answer_create_login() {
        // given
        User loginUser = defaultUser();
        Question question = defaultQuestion();
        String contents = "Hello World";

        // when
        ResponseEntity<String> response = createAnswerResource(loginUser, contents, question.getId());
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);

        // then
        Answer dbAnswer = getAnswerResource(response.getHeaders().getLocation().getPath()).getBody();

        softly.assertThat(dbAnswer.getWriter()).isEqualTo(loginUser);
        softly.assertThat(dbAnswer.getContents()).isEqualTo(contents);
        softly.assertThat(dbAnswer.getQuestion()).isEqualTo(question);
    }

    @Test
    public void answer_delete_no_login() {
        // given
        Answer answer = answerOfDefaultUser();

        // when
        ResponseEntity<String> response = deleteResourceWithoutLogin(answer.generateUrl(), String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);

        Answer dbAnswer = getAnswerResource(answer.generateUrl()).getBody();
        softly.assertThat(dbAnswer.isDeleted()).isFalse();
    }

    @Test
    public void answer_delete_login_작성자() {
        // given
        User loginUser = defaultUser();
        Answer answer = answerOfDefaultUser();

        // when
        ResponseEntity<String> response = deleteAnswerResource(loginUser, answer);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.NO_CONTENT);

        Answer deletedAnswer = getAnswerResource(answer.generateUrl()).getBody();
        softly.assertThat(deletedAnswer.isDeleted()).isTrue();
    }

    @Test
    public void answer_delete_login_DeleteHistory_저장_확인() {
        // given
        User loginUser = defaultUser();
        Answer answer = answerOfDefaultUser();

        // when
        deleteAnswerResource(loginUser, answer);

        // then
        DeleteHistory questionDeleteHistory = findDeleteHistoryByContentTypeAndContentId(ContentType.ANSWER, answer.getId());
        DeleteHistory shouldBeSame = new DeleteHistory(ContentType.ANSWER, answer.getId(), loginUser);
        softly.assertThat(questionDeleteHistory.equalsContentTypeAndContentIdAndDeletedBy(shouldBeSame)).isTrue();
    }

    @Test
    public void answer_update_no_login() {
        // given
        Answer answer = answerOfDefaultUser();

        // when
        String modifiedContents = "Hello World";
        answer.setContents(modifiedContents);

        ResponseEntity<Answer> response = updateResourceWithoutLogin(answer.generateUrl(), modifiedContents, Answer.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);

        Answer dbAnswer = getAnswerResource(answer.generateUrl()).getBody();
        softly.assertThat(dbAnswer.getContents()).isNotEqualTo(modifiedContents);
    }

    @Test
    public void answer_update_login() {
        // given
        User loginUser = defaultUser();
        Answer answer = answerOfDefaultUser();

        // when
        String modifiedContents = "Hello World";
        answer.setContents(modifiedContents);

        ResponseEntity<Answer> response = updateAnswerResource(loginUser, answer.generateUrl(), answer);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        Answer dbAnswer = getAnswerResource(answer.generateUrl()).getBody();
        softly.assertThat(dbAnswer.getContents()).isEqualTo(modifiedContents);
    }

    private Answer answerOfDefaultUser() {
        User loginUser = defaultUser();
        Question question = defaultQuestion();
        String contents = "I'm happy";

        String createdAnswerResourceLocation =
                createAnswerResource(loginUser, contents, question.getId()).getHeaders().getLocation().getPath();

        return getAnswerResource(createdAnswerResourceLocation).getBody();
    }

    private ResponseEntity<Answer> getAnswerResource(String location) {
        ResponseEntity<Answer> response = getResourceWithoutLogin(location, Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        return response;
    }

    private ResponseEntity<String> createAnswerResourceWithoutLogin(String contents, long questionId) {
        Answer answer = new Answer().setContents(contents);
        return createResourceWithoutLogin(String.format("/api/questions/%d/answers", questionId), answer, String.class);
    }

    private ResponseEntity<String> createAnswerResource(User loginUser, String contents, long questionId) {
        Answer answer = new Answer().setContents(contents);
        return createResource(loginUser, String.format("/api/questions/%d/answers", questionId), answer, String.class);
    }

    private ResponseEntity<String> deleteAnswerResource(User loginUser, Answer answer) {
        return deleteResource(loginUser, answer.generateUrl(), String.class);
    }

    private ResponseEntity<Answer> updateAnswerResource(User loginUser, String location, Answer updatedAnswer) {
        return updateResource(loginUser, location, updatedAnswer, Answer.class);
    }
}
