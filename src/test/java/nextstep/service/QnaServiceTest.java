package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.domain.UserTest;
import org.hibernate.service.spi.InjectService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void deleteQuestion() throws CannotDeleteException {
        User user = UserTest.JAVAJIGI;
        long questionId = 1;
        verify(qnaService, times(1))
                .deleteQuestion(user, questionId);
    }

    @Test(expected = Exception.class)
    public void deleteQuestion_throws() throws CannotDeleteException {
        User user = UserTest.JAVAJIGI;
        long questionId = 1;
        doThrow().when(qnaService).deleteQuestion(user, questionId);

        qnaService.deleteQuestion(user, questionId);
    }

    @Test
    public void addAnswer() {
    }

    @Test
    public void deleteAnswer() {
    }
}