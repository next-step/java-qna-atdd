package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnswerServiceTest {
    @InjectMocks
    private AnswerService answerService;

    @Mock(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Mock(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Mock(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    public static Question question;
    public static Answer answer;

    @Before
    public void setUp() throws Exception {
        question = Question.ofList(QuestionTest.dataultQuestionBody(), JAVAJIGI, new ArrayList<>());
        answer = Answer.of(SANJIGI, "내용테스트");
    }
    @Test
    public void addAnswer() {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));
        answerService.addAnswer(JAVAJIGI, question.getId(), "답변내용올리기");
    }
    @Test
    public void deleteAnswer() throws CannotDeleteException {
        when(answerRepository.findById(any())).thenReturn(Optional.ofNullable(answer));
        answerService.deleteAnswer(SANJIGI,answer.getId());
        assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteAnswer타인() throws CannotDeleteException {
        when(answerRepository.findById(any())).thenReturn(Optional.ofNullable(answer));
        answerService.deleteAnswer(JAVAJIGI,answer.getId());
        assertThat(answer.isDeleted()).isTrue();
    }

    @Test
    public void updateAnswer() {
        String contents = "답변내용올리기";
        when(answerRepository.findById(any())).thenReturn(Optional.ofNullable(answer));
        answerService.updateAnswer(SANJIGI,answer.getId(),contents);
        assertThat(answer.getContents()).isEqualTo(contents);
    }


    @Test(expected = UnAuthorizedException.class)
    public void updateAnswer타인() {
        String contents = "답변내용올리기";
        when(answerRepository.findById(any())).thenReturn(Optional.ofNullable(answer));
        answerService.updateAnswer(JAVAJIGI,answer.getId(),contents);
        assertThat(answer.getContents()).isEqualTo(contents);
    }


    @Test(expected = UnAuthorizedException.class)
    public void updateAnswer손님() {
        String contents = "답변내용올리기";
        when(answerRepository.findById(any())).thenReturn(Optional.ofNullable(answer));
        answerService.updateAnswer(User.GUEST_USER,answer.getId(),contents);
        assertThat(answer.getContents()).isEqualTo(contents);
    }
}