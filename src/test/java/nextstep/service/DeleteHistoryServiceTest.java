package nextstep.service;

import nextstep.domain.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Arrays;
import java.util.List;

import static nextstep.domain.AnswerTest.selfAnswer;
import static nextstep.domain.QuestionTest.selfQuestion;
import static nextstep.domain.UserTest.SELF_USER;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteHistoryServiceTest extends BaseTest {

    @Spy
    DeleteHistoryRepository deleteHistoryRepository;

    @Spy
    @InjectMocks
    DeleteHistoryService deleteHistoryService;

    @Test
    public void save_질문1() throws Exception {
        User user = SELF_USER;

        List<DeleteHistory> deleteHistories = selfQuestion().delete(user);
        when(deleteHistoryRepository.findAllByContentType(ContentType.QUESTION))
                .thenReturn(deleteHistories);

        deleteHistoryService.saveAll(selfQuestion().delete(user));

        softly.assertThat(deleteHistoryRepository.findAllByContentType(ContentType.QUESTION))
                .hasSize(1);
    }

    @Test
    public void save_답변1() throws Exception {
        User user = SELF_USER;
        Question question = selfQuestion();
        Answer answer = selfAnswer();
        question.addAnswer(answer);

        when(deleteHistoryRepository.findAllByContentType(ContentType.ANSWER))
                .thenReturn(Arrays.asList(answer.delete(user)));

        answer = selfAnswer();
        question.addAnswer(answer);
        deleteHistoryService.save(answer.delete(user));

        softly.assertThat(deleteHistoryRepository.findAllByContentType(ContentType.ANSWER))
                .hasSize(1);
    }
}