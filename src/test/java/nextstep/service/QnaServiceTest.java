package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.UnAuthenticationException;
import nextstep.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import support.test.BaseTest;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static nextstep.domain.Fixture.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QnaServiceTest extends BaseTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QnaService qnaService;

    private Optional<Question> returnCacheValue;
    private Optional<Answer> returnCacheAnswer;

    @Before
    public void setUp() {
        MOCK_QUESTION.writeBy(MOCK_USER);
        ANSWER.setId(1);
        returnCacheValue = Optional.of((Question) MOCK_QUESTION);
        returnCacheAnswer = Optional.of((Answer) ANSWER);
    }

    @Test
    public void 생성테스트() {
        Question result = qnaService.create(MOCK_USER, MOCK_QUESTION);
        softly.assertThat(result).isEqualTo(MOCK_QUESTION);
        softly.assertThat(result.getWriter()).isNotNull();
    }

    @Test
    public void 업데이트_테스트() throws Exception {
        Question result = qnaService.update(new User("sanjigi", "password", "산지기", "sanjigi@slipp.net"), 2, Question.builder().title("아기상어").contents("아빠상어").build());
        softly.assertThat(result.getTitle()).isEqualTo("아기상어");
        softly.assertThat(result.getContents()).isEqualTo("아빠상어");
        softly.assertThat(result.getWriter()).isNotNull();
    }

    @Test(expected = UnAuthenticationException.class)
    public void 업데이트_실패_테스트() throws Exception {
        Question result = qnaService.update(new User("이상하네", "아무리봐도", "다른데", "왜그래@slipp.net"), 1, MOCK_QUESTION);
        softly.assertThat(result.getContents()).isEqualTo("뚜루루뚜루");
        softly.assertThat(result.getTitle()).isEqualTo("아기상어");
        softly.assertThat(result.getWriter()).isNotNull();
    }


    // TODO : 이거 결과값 체크 필요
    @Test
    public void 삭제_테스트() throws CannotDeleteException {
        qnaService.deleteQuestion(OTHER_USER, 2);
    }

    @Test(expected = EntityNotFoundException.class)
    public void 삭제_실패_테스트() throws CannotDeleteException {
        qnaService.deleteQuestion(new User("sanjigi2", "password", "name", "javajigi@slipp.net"), 0);
    }

    @Test
    public void 답변_추가_테스트() {
        Answer result = qnaService.addAnswer(MOCK_USER, 1, "엄마상어는요!");
        softly.assertThat(result.getContents()).isEqualTo("엄마상어는요!");
    }

    @Test
    public void 답변_수정_테스트() throws Exception {
        Answer result = qnaService.updateAnswer(MOCK_USER, 1L, "여기까지가~~ 끝인가바여!");
        softly.assertThat(result.getContents()).isEqualTo("여기까지가~~ 끝인가바여!");
    }

    @Test(expected = EntityNotFoundException.class)
    public void 답변_수정_실패테스트_없는것삭제() throws Exception {
        qnaService.updateAnswer(MOCK_USER, 7L, "여기까지가~~ 끝인가바여!");
    }

    @Test(expected = UnAuthenticationException.class)
    public void 답변_수정_실패테스트_유저다름() throws Exception {
        qnaService.updateAnswer(new User("hyerin", "password", "hyerin", "hyerin@hyerin.net"), 1L, "여기까지가~~ 끝인가바여!");
    }

    @Test
    public void 답변_삭제_테스트() throws Exception {
        qnaService.deleteAnswer(MOCK_USER, 1);

        boolean result = answerRepository.findById(1L).orElse(null).isDeleted();
        softly.assertThat(result).isEqualTo(true);
    }

    @Test(expected = EntityNotFoundException.class)
    public void 답변_삭제_실패_테스트() throws Exception {
        qnaService.deleteAnswer(MOCK_USER, 0);
    }
}