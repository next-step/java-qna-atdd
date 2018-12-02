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
public class QnaServiceTest  {
    @InjectMocks
    private QnaService qnaService;

    @Mock(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Mock(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Mock(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;
    private static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    private static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    private static Question question;

    private static QuestionBody questionBody;
    private static Answer answer;

    @Before
    public void setUp() throws Exception {
        questionBody = new QuestionBody("제목테스트","내용테스트");
        question = Question.ofList(questionBody, JAVAJIGI, new ArrayList<>());
        answer = Answer.of(SANJIGI, "내용테스트");
    }

    @Test
    public void create() {
        qnaService.createQuestion(JAVAJIGI,  questionBody);
    }

    @Test
    public void update() {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));
        String title = "제목업데이트";
        String contents = "내용업데이트";
        QuestionBody questionBody = new QuestionBody(title,contents);
        qnaService.updateQuestion(question.getWriter(), question.getId(), questionBody);
        assertThat(question.getBody()).isEqualTo(questionBody);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update타인() {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));
        String title = "제목업데이트";
        String contents = "내용업데이트";
        QuestionBody questionBody = new QuestionBody(title, contents);
        qnaService.updateQuestion(SANJIGI, question.getId(),  questionBody);
        assertThat(question.getTitle()).isEqualTo(title);
        assertThat(question.getContents()).isEqualTo(contents);
    }

    @Test
    public void deleteQuestion() throws CannotDeleteException {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));
        qnaService.deleteQuestion(JAVAJIGI, question.getId());
        assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteQuestion타인() throws CannotDeleteException {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));
        qnaService.deleteQuestion(SANJIGI, question.getId());
        assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteQuestion손님() throws CannotDeleteException {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));
        qnaService.deleteQuestion(User.GUEST_USER, question.getId());
        assertThat(question.isDeleted()).isTrue();
    }

}