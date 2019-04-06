package nextstep.web;

import nextstep.domain.Answer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.helper.ApiExecuteBuilder;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);
    private static final String updatedContents = "답변 수정";
    private static final long ID_ONE = 1L;
    private static final long ID_THREE = 3L;
    private static final long ID_NOT_FOUND = 100L;

    @Test
    public void 답변_조회_성공() throws Exception {
        // Given
        long answerId = ID_ONE;

        // When
        ResponseEntity<Answer> response = template().getForEntity(
            String.format("/api/answers/%d", answerId), Answer.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Answer answer = response.getBody();
        softly.assertThat(answer).isNotNull();
        softly.assertThat(answer.getId()).isEqualTo(answerId);
    }

    @Test
    public void 답변_조회_실패_400_없는_질문() throws Exception {
        // Given
        long answerId = ID_NOT_FOUND;

        // When
        ResponseEntity<Answer> response = template().getForEntity(
            String.format("/api/answers/%d", answerId), Answer.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 답변_조회_실패_400_없는_답변() throws Exception {
        // Given
        long answerId = ID_NOT_FOUND;

        // When
        ResponseEntity<Answer> response = template().getForEntity(
            String.format("/api/answers/%d", answerId), Answer.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 답변_조회_실패_400_질문O_답변X() throws Exception {
        // Given
        long answerId = ID_NOT_FOUND;

        // When
        ResponseEntity<Answer> response = template().getForEntity(
            String.format("/api/answers/%d", answerId), Answer.class);


        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 답변_수정_성공() throws Exception {
        //given
        long answerId = ID_THREE;
        Answer original = getResource(String.format("/api/answers/%d", answerId), Answer.class, defaultUser());
        Answer updatedAnswer = new Answer(original.getId(), original.getWriter(), original.getQuestion(), updatedContents);

        //when
        ResponseEntity<Answer> responseEntity =
            ApiExecuteBuilder.setUp(basicAuthTemplate(defaultUser()), Answer.class)
                .url(String.format("/api/answers/%d", answerId))
                .put()
                .request(updatedAnswer)
                .execute();

        Answer responseAnswer = responseEntity.getBody();

        //then
        softly.assertThat(responseAnswer).isNotNull();
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 답변_수정_실패_403_다른_유저() throws Exception {
        //given
        long answerId = ID_THREE;
        Answer original = getResource(String.format("/api/answers/%d", answerId), Answer.class, defaultUser());
        Answer updatedAnswer = new Answer(original.getId(), original.getWriter(), original.getQuestion(), updatedContents);

        //when
        ResponseEntity<Answer> responseEntity =
            ApiExecuteBuilder.setUp(basicAuthTemplate(secondLoginUser()), Answer.class)
                .url(String.format("/api/answers/%d", answerId))
                .put()
                .request(updatedAnswer)
                .execute();

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 답변_수정_실패_401_로그인_안함() throws Exception {
        //given
        long answerId = ID_THREE;
        Answer original = getResource(String.format("/api/answers/%d", answerId), Answer.class, defaultUser());
        Answer updatedAnswer = new Answer(original.getId(), original.getWriter(), original.getQuestion(), updatedContents);

        //when
        ResponseEntity<Answer> responseEntity =
            ApiExecuteBuilder.setUp(template(), Answer.class)
                .url(String.format("/api/answers/%d", answerId))
                .put()
                .request(updatedAnswer)
                .execute();
        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 답변_삭제_성공() throws Exception {
        //given
        long answerId = ID_ONE;

        //when
        ResponseEntity<Void> responseEntity =
            ApiExecuteBuilder.setUp(basicAuthTemplate(defaultUser()), Void.class)
                .url(String.format("/api/answers/%d", answerId))
                .delete()
                .execute();

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 답변_삭제_실패_400_없는_답변() throws Exception {
        //given
        long answerId = ID_NOT_FOUND;

        //when
        ResponseEntity<Void> responseEntity =
            ApiExecuteBuilder.setUp(basicAuthTemplate(defaultUser()), Void.class)
                .url(String.format("/api/answers/%d", answerId))
                .delete()
                .execute();

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    public void 답변_삭제_실패_401_로그인_안한_유저() throws Exception {
        //given
        long answerId = ID_ONE;

        //when
        ResponseEntity<Void> responseEntity =
            ApiExecuteBuilder.setUp(template(), Void.class)
                .url(String.format("/api/answers/%d", answerId))
                .delete()
                .execute();

        //then
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
