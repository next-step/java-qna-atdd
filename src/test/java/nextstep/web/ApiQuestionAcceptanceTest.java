package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);
    private static final Question question = new Question("질문 제목", "질문 내용");
    private static final String originalContents = "답변 등록";
    private static final long ID_ONE = 1L;
    private static final long ID_TWO = 2L;
    private static final long ID_NOT_FOUND = 100L;

    private static Question editQuestion(long id, String title, String content) {
        Question editQuestion = new Question(title, content);
        editQuestion.setId(id);
        return editQuestion;
    }

    @Test
    public void 질문_생성_성공() throws Exception {
        // When
        ResponseEntity<Void> response = basicAuthTemplate(defaultUser())
            .postForEntity("/api/questions", question, Void.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String location = response.getHeaders().getLocation().getPath();
        Question responseQuestion = template().getForObject(location, Question.class);

        softly.assertThat(responseQuestion.getTitle()).isEqualTo(question.getTitle());
        softly.assertThat(responseQuestion.getContents()).isEqualTo(question.getContents());
    }

    @Test
    public void 질문_생성_실패_401_로그인_안함() throws Exception {
        //when
        ResponseEntity<Void> response = template()
            .postForEntity("/api/questions", question, Void.class);

        //then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문_조회_성공() throws Exception {
        // Given
        long questionId = ID_ONE;

        // When
        ResponseEntity<Question> response = template()
            .getForEntity(String.format("/api/questions/%d", questionId), Question.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Question responseQuestion = response.getBody();
        softly.assertThat(responseQuestion.getId()).isEqualTo(questionId);
    }

    @Test
    public void 질문_조회_실패_400_없는_질문() throws Exception {
        // Given
        long questionId = ID_NOT_FOUND;

        // When
        ResponseEntity<Question> response = template()
            .getForEntity(String.format("/api/questions/%d", questionId), Question.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 질문_수정_성공() throws Exception {
        //given
        String location = createResource("/api/questions", question, defaultUser());
        Question original = getResource(location, Question.class, defaultUser());
        Question editedQuestion = editQuestion(original.getId(), "제목 수정", "내용 수정");

        //when
        ResponseEntity<Question> responseEntity = basicAuthTemplate(defaultUser())
            .exchange(location, HttpMethod.PUT, createHttpEntity(editedQuestion), Question.class);

        Question responseQuestion = responseEntity.getBody();

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseQuestion).isEqualTo(editedQuestion);
    }

    @Test
    public void 질문_수정_실패_403_다른_유저() throws Exception {
        //given
        String location = createResource("/api/questions", question, defaultUser());
        Question original = getResource(location, Question.class, defaultUser());
        Question editedQuestion = editQuestion(original.getId(), "제목 수정", "내용 수정");

        //when
        ResponseEntity<Question> responseEntity = basicAuthTemplate(secondLoginUser())
            .exchange(location, HttpMethod.PUT, createHttpEntity(editedQuestion), Question.class);

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 질문_수정_실패_401_권한_없음() throws Exception {
        //given
        String location = createResource("/api/questions", question, defaultUser());
        Question original = getResource(location, Question.class, defaultUser());
        Question editedQuestion = editQuestion(original.getId(), "제목 수정", "내용 수정");

        //when
        ResponseEntity<Question> responseEntity = template()
            .exchange(location, HttpMethod.PUT, createHttpEntity(editedQuestion), Question.class);

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문_삭제_실패_500_답변_존재() throws Exception {
        //given
        long questionId = ID_ONE;

        //when
        ResponseEntity<Void> responseEntity = basicAuthTemplate(defaultUser())
            .exchange(String.format("/api/questions/%d", questionId),
                HttpMethod.DELETE, createHttpEntity(null), Void.class);

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void 질문_삭제_실패_400_없는_질문() throws Exception {
        //given
        long questionId = ID_NOT_FOUND;

        //when
        ResponseEntity<Void> responseEntity = basicAuthTemplate(defaultUser())
            .exchange(String.format("/api/questions/%d", questionId),
                HttpMethod.DELETE, createHttpEntity(null), Void.class);

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 질문_삭제_실패_401_권한_없음() throws Exception {
        //given
        long questionId = ID_ONE;

        //when
        ResponseEntity<Void> responseEntity = template()
            .exchange(String.format("/api/questions/%d", questionId),
                HttpMethod.DELETE, createHttpEntity(null), Void.class);

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 답변_생성_성공() throws Exception {
        // Given
        long questionId = ID_TWO;

        // When
        ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).postForEntity(
            String.format("/api/questions/%d/answers", questionId), originalContents, Void.class);


        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void 답변_생성_실패_400_없는_질문() throws Exception {
        // Given
        long questionId = ID_NOT_FOUND;

        // When
        ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).postForEntity(
            String.format("/api/questions/%d/answers", questionId), originalContents, Void.class);


        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 답변_생성_실패_401_권한_없음() throws Exception {
        // Given
        long questionId = ID_NOT_FOUND;

        // When
        ResponseEntity<Void> response = template().postForEntity(
            String.format("/api/questions/%d/answers", questionId), originalContents, Void.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}