package nextstep.web;

import nextstep.domain.Answer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);
    private static final String originalContents = "답변 등록";
    private static final String updatedContents = "답변 수정";


    @Test
    public void 답변_생성_성공() throws Exception {
        // Given
        long questionId = 1L;

        // When
        ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).postForEntity(
            String.format("/api/questions/%d/answers", questionId), originalContents, Void.class);


        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String location = response.getHeaders().getLocation().getPath();
        Answer dbAnswer = template().getForObject(location, Answer.class);

        softly.assertThat(dbAnswer.getQuestion().getId()).isEqualTo(questionId);
        softly.assertThat(dbAnswer.getContents()).isEqualTo(originalContents);
    }

    @Test
    public void 답변_생성_실패_400_없는_질문() throws Exception {
        // Given
        long questionId = 5L;

        // When
        ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).postForEntity(
            String.format("/api/questions/%d/answers", questionId), originalContents, Void.class);


        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 답변_생성_실패_401_권한_없음() throws Exception {
        // Given
        long questionId = 5L;

        // When
        ResponseEntity<Void> response = template().postForEntity(
            String.format("/api/questions/%d/answers", questionId), originalContents, Void.class);


        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 답변_조회_성공() throws Exception {
        // Given
        long questionId = 1L;
        long answerId = 1L;

        // When
        ResponseEntity<Answer> response = template().getForEntity(
            String.format("/api/questions/%d/answers/%d", questionId, answerId), Answer.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Answer answer = response.getBody();
        softly.assertThat(answer).isNotNull();
        softly.assertThat(answer.getId()).isEqualTo(answerId);
        softly.assertThat(answer.getQuestion().getId()).isEqualTo(questionId);
    }

    @Test
    public void 답변_조회_실패_400_없는_질문() throws Exception {
        // Given
        long questionId = 5L;
        long answerId = 1L;

        // When
        ResponseEntity<Answer> response = template().getForEntity(
            String.format("/api/questions/%d/answers/%d", questionId, answerId), Answer.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 답변_조회_실패_400_없는_답변() throws Exception {
        // Given
        long questionId = 1L;
        long answerId = 5L;

        // When
        ResponseEntity<Answer> response = template().getForEntity(
            String.format("/api/questions/%d/answers/%d", questionId, answerId), Answer.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 답변_조회_실패_400_질문O_답변X() throws Exception {
        // Given
        long questionId = 2L;
        long answerId = 5L;

        // When
        ResponseEntity<Answer> response = template().getForEntity(
            String.format("/api/questions/%d/answers/%d", questionId, answerId), Answer.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 답변_수정_성공() throws Exception {
        //given
        long questionId = 1L;
        String location = createResource(String.format("/api/questions/%d/answers", questionId), originalContents, defaultUser());
        Answer original = getResource(location, Answer.class, defaultUser());
        Answer updatedAnswer = new Answer(original.getId(), original.getWriter(), original.getQuestion(), updatedContents);

        //when
        ResponseEntity<Answer> responseEntity =
            basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.PUT, createHttpEntity(updatedAnswer), Answer.class);
        Answer responseAnswer = responseEntity.getBody();

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseAnswer).isNotNull();
        softly.assertThat(responseAnswer).isEqualTo(updatedAnswer);
    }

    @Test
    public void 답변_수정_실패_403_다른_유저() throws Exception {
        //given
        long questionId = 1L;
        String location = createResource(String.format("/api/questions/%d/answers", questionId), originalContents, defaultUser());
        Answer original = getResource(location, Answer.class, defaultUser());
        Answer updatedAnswer = new Answer(original.getId(), original.getWriter(), original.getQuestion(), updatedContents);

        //when
        ResponseEntity<Answer> responseEntity =
            basicAuthTemplate(secondLoginUser()).exchange(location, HttpMethod.PUT, createHttpEntity(updatedAnswer), Answer.class);

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 답변_수정_실패_401_로그인_안함() throws Exception {
        //given
        long questionId = 1L;
        String location = createResource(String.format("/api/questions/%d/answers", questionId), originalContents, defaultUser());
        Answer original = getResource(location, Answer.class, defaultUser());
        Answer updatedAnswer = new Answer(original.getId(), original.getWriter(), original.getQuestion(), updatedContents);

        //when
        ResponseEntity<Answer> responseEntity =
            template().exchange(location, HttpMethod.PUT, createHttpEntity(updatedAnswer), Answer.class);

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 답변_삭제_성공() throws Exception {
        //given
        long questionId = 1L;
        long answerId = 1L;

        //when
        ResponseEntity<Void> responseEntity = basicAuthTemplate(defaultUser())
            .exchange(String.format("/api/questions/%d/answers/%d", questionId, answerId),
                HttpMethod.DELETE, createHttpEntity(null), Void.class);

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 답변_삭제_실패_400_없는_질문() throws Exception {
        //given
        long questionId = 5L;
        long answerId = 1L;

        //when
        ResponseEntity<Void> responseEntity = basicAuthTemplate(defaultUser())
            .exchange(String.format("/api/questions/%d/answers/%d", questionId, answerId),
                HttpMethod.DELETE, createHttpEntity(null), Void.class);

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 답변_삭제_실패_400_없는_답변() throws Exception {
        //given
        long questionId = 1L;
        long answerId = 5L;

        //when
        ResponseEntity<Void> responseEntity = basicAuthTemplate(defaultUser())
            .exchange(String.format("/api/questions/%d/answers/%d", questionId, answerId),
                HttpMethod.DELETE, createHttpEntity(null), Void.class);

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 답변_삭제_실패_400_질문O_답변X() throws Exception {
        //given
        long questionId = 2L;
        long answerId = 1L;

        //when
        ResponseEntity<Void> responseEntity = basicAuthTemplate(defaultUser())
            .exchange(String.format("/api/questions/%d/answers/%d", questionId, answerId),
                HttpMethod.DELETE, createHttpEntity(null), Void.class);

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 답변_삭제_실패_401_로그인_안한_유저() throws Exception {
        //given
        long questionId = 1L;
        long answerId = 1L;

        //when
        ResponseEntity<Void> responseEntity = template()
            .exchange(String.format("/api/questions/%d/answers/%d", questionId, answerId),
                HttpMethod.DELETE, createHttpEntity(null), Void.class);

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
