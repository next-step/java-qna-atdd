package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.UnAuthenticationException;
import nextstep.domain.*;
import org.junit.After;
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
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QnaService qnaService;

    @Autowired
    private DeleteHistoryRepository deleteHistoryRepository;

    private Question initQuestion;
    private User javajigiUser;
    private User sanjigiUser;

    @Before
    public void setUp() {
        javajigiUser = userRepository.findByUserId("javajigi").orElseThrow(EntityNotFoundException::new);
        sanjigiUser = userRepository.findByUserId("sanjigi").orElseThrow(EntityNotFoundException::new);
    }

    @Test
    public void 질문_생성테스트() {
        Question result = qnaService.create(javajigiUser, MOCK_QUESTION);
        MOCK_QUESTION.writeBy(javajigiUser);

        softly.assertThat(result.getWriter()).isNotNull();
        softly.assertThat(result.getWriter()).isEqualTo(javajigiUser);
        softly.assertThat(result.getTitle()).isEqualTo(MOCK_QUESTION.getTitle());
        softly.assertThat(result.getContents()).isEqualTo(MOCK_QUESTION.getContents());
    }

    @Test
    public void 질문_업데이트_테스트() throws Exception {
        initQuestion = qnaService.create(javajigiUser, MOCK_QUESTION);

        Question result = qnaService.update(javajigiUser, initQuestion.getId(), Question.builder().title("아기상어").contents("아빠상어").build());
        softly.assertThat(result.getTitle()).isEqualTo("아기상어");
        softly.assertThat(result.getContents()).isEqualTo("아빠상어");
        softly.assertThat(result.getWriter()).isNotNull();
    }

    @Test(expected = UnAuthenticationException.class)
    public void 질문_업데이트_실패_테스트_유저다름() throws Exception {
       initQuestion = qnaService.create(javajigiUser, MOCK_QUESTION);

        Question result = qnaService.update(sanjigiUser, initQuestion.getId(), MOCK_QUESTION);
        softly.assertThat(result.getContents()).isEqualTo("뚜루루뚜루");
        softly.assertThat(result.getTitle()).isEqualTo("아기상어");
        softly.assertThat(result.getWriter()).isNotNull();
    }

    @Test
    public void 질문_삭제_성공_테스트_답변0개() throws CannotDeleteException {
        initQuestion = qnaService.create(javajigiUser, MOCK_QUESTION);
        qnaService.deleteQuestion(MOCK_USER, initQuestion.getId());
        softly.assertThat(qnaService.findById(initQuestion.getId()).orElseGet(null).isDeleted()).isTrue();

        Optional<DeleteHistory> deleteResult = deleteHistoryRepository.findByContentId(initQuestion.getId());
        DeleteHistory deleteHistory = deleteResult.orElseThrow(EntityNotFoundException::new);

        softly.assertThat(deleteHistory.getContentType()).isEqualTo(ContentType.QUESTION);
        softly.assertThat(deleteHistory.getContentId()).isEqualTo(initQuestion.getId());
        softly.assertThat(deleteHistory.getDeletedBy()).isEqualTo(javajigiUser);
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문_삭제_실패_테스트_답변있음() throws CannotDeleteException {
        initQuestion = qnaService.create(javajigiUser, MOCK_QUESTION);
        qnaService.addAnswer(sanjigiUser, initQuestion.getId(), "엄마상어는요!");

        qnaService.deleteQuestion(javajigiUser, initQuestion.getId());
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문_삭제_실패_테스트_유저다름() throws CannotDeleteException {
        initQuestion = qnaService.create(javajigiUser, MOCK_QUESTION);
        qnaService.deleteQuestion(sanjigiUser, initQuestion.getId());
    }

    @Test
    public void 답변_추가_테스트() {
        initQuestion = qnaService.create(javajigiUser, MOCK_QUESTION);
        Answer result = qnaService.addAnswer(javajigiUser, initQuestion.getId(), "엄마상어는요!");
        softly.assertThat(result.getContents()).isEqualTo("엄마상어는요!");
        softly.assertThat(result.getId()).isEqualTo(result.getId());
    }

    @Test
    public void 답변_수정_테스트() throws Exception {
        initQuestion = qnaService.create(javajigiUser, MOCK_QUESTION);
        Answer result = qnaService.addAnswer(javajigiUser, initQuestion.getId(), "엄마상어는요!");

        Answer updateResult = qnaService.updateAnswer(javajigiUser, result.getId(), "여기까지가~~ 끝인가바여!");
        softly.assertThat(updateResult.getContents()).isEqualTo("여기까지가~~ 끝인가바여!");
    }

    @Test(expected = EntityNotFoundException.class)
    public void 답변_수정_실패테스트_없는것삭제() throws Exception {
        qnaService.updateAnswer(MOCK_USER, 7L, "여기까지가~~ 끝인가바여!");
    }

    @Test(expected = UnAuthenticationException.class)
    public void 답변_수정_실패테스트_유저다름() throws Exception {
        initQuestion = qnaService.create(javajigiUser, MOCK_QUESTION);
        Answer result = qnaService.addAnswer(javajigiUser, initQuestion.getId(), "엄마상어는요!");

        qnaService.updateAnswer(sanjigiUser, result.getId(), "여기까지가~~ 끝인가바여!");
    }

    @Test
    public void 답변_삭제_테스트() throws Exception {
        initQuestion = qnaService.create(javajigiUser, MOCK_QUESTION);
        Answer result = qnaService.addAnswer(javajigiUser, initQuestion.getId(), "엄마상어는요!");

        qnaService.deleteAnswer(MOCK_USER, result.getId());

        boolean deleteResult = answerRepository.findById(result.getId()).orElse(null).isDeleted();
        softly.assertThat(deleteResult).isEqualTo(true);

        Optional<DeleteHistory> deleteHistoryResult = deleteHistoryRepository.findByContentId(result.getId());
        DeleteHistory deleteHistory = deleteHistoryResult.orElseThrow(EntityNotFoundException::new);

        softly.assertThat(deleteHistory.getContentType()).isEqualTo(ContentType.ANSWER);
        softly.assertThat(deleteHistory.getContentId()).isEqualTo(result.getId());
        softly.assertThat(deleteHistory.getDeletedBy()).isEqualTo(javajigiUser);
    }

    @Test(expected = EntityNotFoundException.class)
    public void 답변_삭제_실패_테스트() throws Exception {
        qnaService.deleteAnswer(MOCK_USER, 0);
    }

    @After
    public void tearDown() {
        initQuestion = null;
        answerRepository.deleteAll();
        questionRepository.deleteAll();
    }
}