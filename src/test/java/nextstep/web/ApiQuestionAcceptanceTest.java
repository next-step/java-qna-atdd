package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.web.dto.QuestionRequestDTO;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.helper.ApiExecuteBuilder;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);
    private static final Question question = new Question("질문 제목", "질문 내용");
    private static final String originalContents = "답변 등록";
    private static final long ID_ONE = 1L;
    private static final long ID_TWO = 2L;
    private static final long ID_NOT_FOUND = 100L;
    private static final QuestionRequestDTO updatedQuestion = QuestionRequestDTO.builder()
        .title("제목 수정")
        .contents("내용 수정")
        .build();

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

        //when
        ResponseEntity<Question> responseEntity = basicAuthTemplate(defaultUser())
            .exchange(location, HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);

        Question responseQuestion = responseEntity.getBody();

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseQuestion.getTitle()).isEqualTo(updatedQuestion.getTitle());
        softly.assertThat(responseQuestion.getContents()).isEqualTo(updatedQuestion.getContents());
    }

    @Test
    public void 질문_수정_실패_403_다른_유저() throws Exception {
        //given
        String location = createResource("/api/questions", question, defaultUser());
        Question original = getResource(location, Question.class, defaultUser());

        //when
        ResponseEntity<Question> responseEntity = basicAuthTemplate(secondLoginUser())
            .exchange(location, HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 질문_수정_실패_401_권한_없음() throws Exception {
        //given
        String location = createResource("/api/questions", question, defaultUser());
        Question original = getResource(location, Question.class, defaultUser());

        //when
        ResponseEntity<Question> responseEntity = template()
            .exchange(location, HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
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
    public void 질문_삭제_실패_다른사용자_답변보유() {
        //given
        String location = questionPostTest();
        answerPostTest(defaultUser(), location);
        answerPostTest(secondLoginUser(), location);

        //when
        ResponseEntity<Void> responseEntity =
            ApiExecuteBuilder.setUp(basicAuthTemplate(defaultUser()), Void.class)
                .url(location)
                .delete()
                .execute();

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    public void 질문_삭제_가능_같은사용자_답변만() {
        //given
        String location = questionPostTest();
        answerPostTest(defaultUser(), location);
        answerPostTest(defaultUser(), location);
        //when
        ResponseEntity<Void> responseEntity =
            ApiExecuteBuilder.setUp(basicAuthTemplate(defaultUser()), Void.class)
                .url(location)
                .delete()
                .execute();

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Question dbQuestion = basicAuthTemplate(defaultUser()).getForObject(location, Question.class);
        softly.assertThat(dbQuestion).isNull();
    }

    private String questionPostTest() {
        ResponseEntity<Void> questionResponse = basicAuthTemplate(defaultUser())
            .postForEntity("/api/questions", question, Void.class);
        return questionResponse.getHeaders().getLocation().getPath();
    }

    private void answerPostTest(User user, String location) {
        basicAuthTemplate(user).postForEntity(location + "/answers", originalContents, Void.class);
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