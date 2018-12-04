package nextstep.service;

import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private DeleteHistoryService deleteHistoryService;

    @InjectMocks
    private QnaService qnaService;

    private Question basicQuestion;

    @Before
    public void setup() {
        basicQuestion = new Question("타이틀", "컨텐츠", basicUser);
    }

    @Test(expected = UnAuthorizedException.class)
    public void getAuthorizedQuestionWithoutAuthorized() {
        when(questionRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(basicQuestion));

        qnaService.findById(0).hasAuthority(anotherUser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteQuestion_질문이존재하지않는경우() {
        when(questionRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        qnaService.deleteQuestion(basicUser, 0);
    }

    @Test
    public void deleteQuestion_히스토리저장() {
        // given
        Answer answer = new Answer(basicUser, "test");
        basicQuestion.addAnswer(answer);

        when(questionRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(basicQuestion));

        // when
        qnaService.deleteQuestion(basicUser, 0);

        // capture
        ArgumentCaptor<List<DeleteHistory>> deleteHistoriesCapture = ArgumentCaptor.forClass(List.class);
        verify(deleteHistoryService).saveAll(deleteHistoriesCapture.capture());

        // then
        List<DeleteHistory> deleteHistories = deleteHistoriesCapture.getValue();
        softly.assertThat(deleteHistories).extracting("contentType", "deletedBy")
                .containsExactly(
                        tuple(ContentType.QUESTION, basicUser),
                        tuple(ContentType.ANSWER, basicUser)
                );
    }
}
