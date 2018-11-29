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

    @InjectMocks
    private UserService userService;

    @Mock(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Mock(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Mock(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;
    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    public static User user ;
    public static Question question;
    public static Answer answer;

    @Before
    public void setUp() throws Exception {
        question = Question.ofList("제목테스트","내용테스트", JAVAJIGI, new ArrayList<>());
        answer = Answer.of(SANJIGI, "내용테스트");
    }

    @Test
    public void create() {
        qnaService.createQuestion(JAVAJIGI,  question);
    }

    @Test
    public void update() {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));
        String title = "제목업데이트";
        String contents = "내용업데이트";
        qnaService.updateQuestion(question.getWriter(), question.getId(), Question.ofList(title, contents, JAVAJIGI, new ArrayList<>()));
        assertThat(question.getTitle()).isEqualTo(title);
        assertThat(question.getContents()).isEqualTo(contents);

    }

    @Test(expected = UnAuthorizedException.class)
    public void update타인() {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));
        String title = "제목업데이트";
        String contents = "내용업데이트";
        qnaService.updateQuestion(SANJIGI, question.getId(),  Question.ofList(title,contents,JAVAJIGI, new ArrayList<>()));
        assertThat(question.getTitle()).isEqualTo(title);
        assertThat(question.getContents()).isEqualTo(contents);
    }

    @Test
    public void deleteQuestion() throws CannotDeleteException {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));
        qnaService.deleteQuestion(JAVAJIGI, question.getId());
        assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestion타인() throws CannotDeleteException {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));
        qnaService.deleteQuestion(SANJIGI, question.getId());
        assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestion손님() throws CannotDeleteException {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));
        qnaService.deleteQuestion(User.GUEST_USER, question.getId());
        assertThat(question.isDeleted()).isTrue();
    }

    @Test
    public void addAnswer() {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));
        qnaService.addAnswer(JAVAJIGI, question.getId(), "답변내용올리기");
    }
    @Test
    public void deleteAnswer() throws CannotDeleteException {
        when(answerRepository.findById(any())).thenReturn(Optional.ofNullable(answer));
        qnaService.deleteAnswer(SANJIGI,answer.getId());
        assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteAnswer타인() throws CannotDeleteException {
        when(answerRepository.findById(any())).thenReturn(Optional.ofNullable(answer));
        qnaService.deleteAnswer(JAVAJIGI,answer.getId());
        assertThat(answer.isDeleted()).isTrue();
    }

    @Test
    public void updateAnswer() {
        String contents = "답변내용올리기";
        when(answerRepository.findById(any())).thenReturn(Optional.ofNullable(answer));
        qnaService.updateAnswer(SANJIGI,answer.getId(),contents);
        assertThat(answer.getContents()).isEqualTo(contents);
    }


    @Test(expected = UnAuthorizedException.class)
    public void updateAnswer타인() {
        String contents = "답변내용올리기";
        when(answerRepository.findById(any())).thenReturn(Optional.ofNullable(answer));
        qnaService.updateAnswer(JAVAJIGI,answer.getId(),contents);
        assertThat(answer.getContents()).isEqualTo(contents);
    }


    @Test(expected = UnAuthorizedException.class)
    public void updateAnswer손님() {
        String contents = "답변내용올리기";
        when(answerRepository.findById(any())).thenReturn(Optional.ofNullable(answer));
        qnaService.updateAnswer(User.GUEST_USER,answer.getId(),contents);
        assertThat(answer.getContents()).isEqualTo(contents);
    }
}