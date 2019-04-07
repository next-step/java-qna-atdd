package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import support.test.BaseTest;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QnaServiceTest extends BaseTest {

    @Autowired
    private QnaService qnaService;

    @Autowired
    private UserService userService;

    private User loginUser;
    private Question question;

    @Before
    public void setUp() {
        loginUser = userService.findByUserId("javajigi");
        question = qnaService.createQuestion(loginUser, new Question("Question 제목", "본문 내용~~~"));
    }

    @After
    public void tearDown() throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, question.getId());
    }

    @Test
    public void update_question_success() {
        // Given :: setUp
        Question updated = question.setTitle("수정한 제목").setContents("수정한 본문 내용");

        // When
        Question result = qnaService.update(loginUser, question.getId(), updated);

        // Then
        softly.assertThat(result.equalsTitleAndContents(updated)).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_question_다른사람() {
        // Given :: setUp
        Question updated = question.setTitle("수정한 제목").setContents("수정한 본문 내용");
        User other = userService.findByUserId("sanjigi");

        // When
        qnaService.update(other, question.getId(), updated);

        // Then :: expected
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_question_삭제된_질문() throws CannotDeleteException {
        // Given
        Question question = qnaService.createQuestion(loginUser, new Question("삭제할 질문이에요.", "본문 내용~~~"));
        qnaService.deleteQuestion(loginUser, question.getId());

        // When
        qnaService.update(loginUser, question.getId(), new Question("제목 수정!", "본문 수정 ~~"));

        // Then :: expected
    }

    @Test
    public void delete_question() throws CannotDeleteException {
        // Given
        Question question = qnaService.createQuestion(loginUser, new Question("삭제할 질문이에요.", "본문 내용~~~"));

        // When
        qnaService.deleteQuestion(loginUser, question.getId());

        // Then
        Question deleted = qnaService.findById(question.getId());
        softly.assertThat(deleted.isDeleted()).isTrue();
    }

    @Test
    public void delete_question_삭제_이력() throws CannotDeleteException {
        // Given
        Question question = qnaService.createQuestion(loginUser, new Question("삭제할 질문이에요.", "본문 내용~~~"));

        // When
        List<DeleteHistory> deleteHistories = qnaService.deleteQuestion(loginUser, question.getId());
        DeleteHistory deleteQuestionHistory = deleteHistories.get(0);

        // Then
        softly.assertThat(deleteQuestionHistory.isDeleteHistoryOf(ContentType.QUESTION, question.getId())).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_question_다른사람() throws CannotDeleteException {
        // Given
        Question question = qnaService.createQuestion(loginUser, new Question("삭제할 질문이에요.", "본문 내용~~~"));

        // Given :: setUp
        qnaService.deleteQuestion(loginUser, question.getId());
        User other = userService.findByUserId("sanjigi");

        // When
        qnaService.deleteQuestion(other, question.getId());

        // Then :: expected
    }

    @Test
    public void delete_question_질문_답변_작성자_모두같음() throws CannotDeleteException {
        // Given
        Question question = qnaService.createQuestion(loginUser, new Question("삭제할 질문이에요.", "본문 내용~~~"));
        Answer savedAnswer = qnaService.addAnswer(loginUser, question.getId(), new Answer("Good~~"));

        // When
        qnaService.deleteQuestion(loginUser, question.getId());

        // Then
        softly.assertThat(qnaService.findById(question.getId()).isDeleted()).isTrue();
    }

    @Test
    public void delete_question_질문_답변_작성자_모두같음_삭제이력() throws CannotDeleteException {
        // Given
        Question question = qnaService.createQuestion(loginUser, new Question("삭제할 질문이에요.", "본문 내용~~~"));
        Answer savedAnswer = qnaService.addAnswer(loginUser, question.getId(), new Answer("Good~~"));

        // When
        List<DeleteHistory> deleteHistories = qnaService.deleteQuestion(loginUser, question.getId());

        // Then
        softly.assertThat(deleteHistories.size()).isEqualTo(2);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_question_질문_답변_작성자_다름() throws CannotDeleteException {
        // Given
        Question question = qnaService.createQuestion(loginUser, new Question("삭제할 질문이에요.", "본문 내용~~~"));
        User other = userService.findByUserId("sanjigi");
        qnaService.addAnswer(other, question.getId(), new Answer("답변 입니다! 이제 삭제할 수 없어요."));

        // When
        qnaService.deleteQuestion(loginUser, question.getId());

        // Then :: expected
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_question_삭제된질문() throws CannotDeleteException {
        // Given
        Question question = qnaService.createQuestion(loginUser, new Question("삭제할 질문이에요.", "본문 내용~~~"));
        qnaService.deleteQuestion(loginUser, question.getId());

        // When
        qnaService.deleteQuestion(loginUser, question.getId());

        // Then :: expected
    }

    @Test
    public void add_answer() {
        // Given :: setUp
        Answer answer = new Answer("Good~~");

        // When
        Answer savedAnswer = qnaService.addAnswer(loginUser, question.getId(), answer);

        // Then
        softly.assertThat(savedAnswer.isOwner(loginUser)).isEqualTo(true);
        softly.assertThat(savedAnswer.equalsContents(answer)).isEqualTo(true);
        softly.assertThat(savedAnswer.getQuestion().equals(question)).isEqualTo(true);
    }

    @Test
    public void delete_answer() {
        // Given :: setUp
        Answer savedAnswer = qnaService.addAnswer(loginUser, question.getId(), new Answer("Good~~"));

        // When
        qnaService.deleteAnswer(loginUser, question.getId(), savedAnswer.getId());
    }

    @Test
    public void delete_answer_삭제이력() {
        // Given :: setUp
        Answer savedAnswer = qnaService.addAnswer(loginUser, question.getId(), new Answer("Good~~"));

        // When
        DeleteHistory deleteHistory = qnaService.deleteAnswer(loginUser, question.getId(), savedAnswer.getId());

        // Then
        softly.assertThat(deleteHistory.isDeleteHistoryOf(ContentType.ANSWER, savedAnswer.getId())).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_answer_다른사람() {
        // Given :: setUp
        Answer savedAnswer = qnaService.addAnswer(loginUser, question.getId(), new Answer("Good~~"));
        User other = userService.findByUserId("sanjigi");

        // When
        qnaService.deleteAnswer(other, question.getId(), savedAnswer.getId());

        // Then :: expected
    }
}