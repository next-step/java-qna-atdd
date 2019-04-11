package nextstep.web;

import nextstep.domain.ContentType;
import nextstep.domain.DeleteHistory;
import nextstep.domain.Question;
import nextstep.domain.User;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Test
    public void question_read_no_login() {
        Question question = questionOfDefaultUser();

        // when
        ResponseEntity<Question> response = getQuestionResource(question.generateRestUrl());

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        Question dbQuestion = response.getBody();
        softly.assertThat(dbQuestion).isEqualTo(question);
    }

    @Test
    public void question_create_no_login() {
        // given
        String title = "Hello";
        String contents = "World";
        Question question = new Question(title, contents);

        // when
        ResponseEntity<Question> response = createResourceWithoutLogin("/api/questions", question, Question.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void question_create_login() {
        // given
        User loginUser = defaultUser();
        String title = "Hello";
        String contents = "World";
        Question question = new Question(title, contents);

        // when
        ResponseEntity<String> response = createQuestionResource(loginUser, question);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);

        Question dbQuestion = getQuestionResource(response.getHeaders().getLocation().getPath()).getBody();
        softly.assertThat(dbQuestion.getTitle()).isEqualTo(title);
        softly.assertThat(dbQuestion.getContents()).isEqualTo(contents);
        softly.assertThat(dbQuestion.isOwner(loginUser));
    }

    @Test
    public void question_update_no_login() {
        // given
        Question question = questionOfDefaultUser();

        // when
        String updatedTitle = "Hello";
        String updatedContents = "World";
        question.setTitle(updatedTitle);
        question.setContents(updatedContents);

        ResponseEntity<Question> response = updateResourceWithoutLogin(question.generateRestUrl(), question, Question.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);

        Question dbQuestion = getQuestionResource(question.generateRestUrl()).getBody();
        softly.assertThat(dbQuestion.getTitle()).isNotEqualTo(updatedTitle);
        softly.assertThat(dbQuestion.getContents()).isNotEqualTo(updatedContents);
    }

    @Test
    public void question_update_login() {
        // given
        User loginUser = defaultUser();
        Question question = questionOfDefaultUser();

        // when
        String updatedTitle = "Hello";
        String updatedContents = "World";
        question.setTitle(updatedTitle);
        question.setContents(updatedContents);

        ResponseEntity<Question> response = updateQuestionResource(loginUser, question.generateRestUrl(), question);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        Question dbQuestion = getQuestionResource(question.generateRestUrl()).getBody();
        softly.assertThat(dbQuestion.getTitle()).isEqualTo(updatedTitle);
        softly.assertThat(dbQuestion.getContents()).isEqualTo(updatedContents);
    }

    @Test
    public void question_delete_no_login() {
        // given
        Question question = questionOfDefaultUser();

        // when
        ResponseEntity<String> response = deleteResourceWithoutLogin(question.generateRestUrl(), String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);

        Question dbQuestion = getQuestionResource(question.generateRestUrl()).getBody();
        softly.assertThat(dbQuestion.isDeleted()).isFalse();
    }

    @Test
    public void question_delete_login() {
        // given
        User loginUser = defaultUser();
        Question question = questionOfDefaultUser();

        // when
        ResponseEntity<String> response = deleteQuestionResource(loginUser, question);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.NO_CONTENT);

        Question dbQuestion = getQuestionResource(question.generateRestUrl()).getBody();
        softly.assertThat(dbQuestion.isDeleted()).isTrue();
    }

    @Test
    public void question_delete_login_DeleteHistory_저장_확인() {
        // given
        User loginUser = defaultUser();
        Question question = questionOfDefaultUser();

        // when
        deleteQuestionResource(loginUser, question);

        // then
        DeleteHistory questionDeleteHistory = findDeleteHistoryByContentTypeAndContentId(ContentType.QUESTION, question.getId());
        DeleteHistory shouldBeSame = new DeleteHistory(ContentType.QUESTION, question.getId(), loginUser);
        softly.assertThat(questionDeleteHistory).isEqualTo(shouldBeSame);
    }

    private Question questionOfDefaultUser() {
        User loginUser = defaultUser();
        Question question = new Question("Java", "SpringBoot");
        question.writeBy(loginUser);

        String createdQuestionResourceLocation =
                createQuestionResource(loginUser, question).getHeaders().getLocation().getPath();

        return getQuestionResource(createdQuestionResourceLocation).getBody();
    }

    private ResponseEntity<Question> getQuestionResource(String location) {
        return getResourceWithoutLogin(location, Question.class);
    }

    private ResponseEntity<String> createQuestionResource(User loginUser, Question question) {
        return createResource(loginUser, "/api/questions", question, String.class);
    }

    private ResponseEntity<Question> updateQuestionResource(User loginUser, String location, Question updatedQuestion) {
        return updateResource(loginUser, location, updatedQuestion, Question.class);
    }

    private ResponseEntity<String> deleteQuestionResource(User loginUser, Question question) {
        return deleteResource(loginUser, String.format("/api/questions/%d", question.getId()), String.class);
    }
}
