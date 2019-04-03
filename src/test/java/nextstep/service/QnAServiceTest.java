package nextstep.service;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.web.exception.ForbiddenException;
import nextstep.web.exception.NotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnAServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnAService qnAService;

    @Test
    public void 질문_목록을_조회한다() {
        when(questionRepository.findAll()).thenReturn(Arrays.asList(
            new Question("This is title1", "This is contents1"),
            new Question("This is title2", "This is contents2"),
            new Question("This is title3", "This is contents3")));

        List<Question> list = qnAService.findAll();
        softly.assertThat(list).hasSize(3);
    }

    @Test
    public void 질문을_상세조회한다() {
        when(questionRepository.findById(1L)).thenReturn(
            Optional.of(new Question("This is title", "This is contents")));

        Question question = qnAService.findById(1L);
        softly.assertThat(question.getTitle()).isEqualTo("This is title");
    }

    @Test(expected = ForbiddenException.class)
    public void 작성자가_아니면_질문을_조회할수없다() {
        User user = new User("myId", "myPassword", "myName", "myEmail");
        Question question = new Question("This is title", "This is contents");
        question.writeBy(user);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnAService.findByOwner(mock(User.class), 1);
    }

    @Test(expected = NotFoundException.class)
    public void 없는_질문이면_예외가_발생한다() {
        qnAService.findById(1L);
    }

    @Test
    public void 질문을_등록한다() {
        User user = new User("myId", "myPassword", "myName", "myEmail");
        Question question = new Question("This is title", "This is contents");

        qnAService.create(user, question);

        softly.assertThat(question.getWriter()).isEqualTo(user);
        softly.assertThat(question.getTitle()).isEqualTo("This is title");
        softly.assertThat(question.getContents()).isEqualTo("This is contents");
    }

    @Test
    public void 질문을_수정한다() {
        User user = new User("myId", "myPassword", "myName", "myEmail");
        Question question = new Question("This is title", "This is contents");
        question.writeBy(user);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        Question beUpdatedQuestion = new Question("This is updated title", "This is updated contents");
        question = qnAService.update(user, 1L, beUpdatedQuestion);

        softly.assertThat(question.getWriter()).isEqualTo(user);
        softly.assertThat(question.getTitle()).isEqualTo("This is updated title");
        softly.assertThat(question.getContents()).isEqualTo("This is updated contents");
    }

    @Test(expected = ForbiddenException.class)
    public void 작성자가_아닌사람이_질문을_수정하면_예외가_발생한다() {
        User user = new User(1L, "myId", "myPassword", "myName", "myEmail");
        Question question = new Question("This is title", "This is contents");
        question.writeBy(user);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        User anotherUser = new User(2L, "yourId", "yourPassword", "yourName", "yourEmail");
        Question beUpdatedQuestion = new Question("This is updated title", "This is updated contents");
        qnAService.update(anotherUser, 1L, beUpdatedQuestion);
    }

    @Test
    public void 질문을_삭제한다() {
        User user = new User("myId", "myPassword", "myName", "myEmail");
        Question question = new Question("This is title", "This is contents");
        question.writeBy(user);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnAService.deleteQuestion(user, 1L);
    }

    @Test(expected = ForbiddenException.class)
    public void 작성자가_아닌사람이_질문을_삭제하면_예외가_발생한다() {
        User user = new User(1L, "myId", "myPassword", "myName", "myEmail");
        Question question = new Question("This is title", "This is contents");
        question.writeBy(user);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        User anotherUser = new User(2L, "yourId", "yourPassword", "yourName", "yourEmail");
        qnAService.deleteQuestion(anotherUser, 1L);
    }
}
