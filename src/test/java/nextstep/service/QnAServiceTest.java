package nextstep.service;

import nextstep.NotFoundException;
import nextstep.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import support.test.BaseTest;

import java.util.List;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@DataJpaTest
public class QnAServiceTest extends BaseTest {
    @Autowired private QuestionRepository questionRepository;
    @Autowired private QnAService qnAService;

    @Before
    public void setUp() {
        User writer = UserTest.newUser(1L);
        IntStream.rangeClosed(1, 10)
            .mapToObj(i -> new QuestionBody("This is title" + i, "This is contents" + i))
            .map(qb -> new Question(writer, qb))
            .forEach(questionRepository::save);
    }

    @Test
    public void 질문을_등록한다() {
        User writer = UserTest.newUser(1L);
        QuestionBody questionBody = new QuestionBody("This is title", "This is contents");

        Question question = qnAService.createQuestion(writer, questionBody);

        softly.assertThat(question.getQuestionBody()).isEqualTo(questionBody);
    }

    @Test
    public void 질문_목록을_조회한다() {
        List<Question> list = qnAService.findAll();
        softly.assertThat(list).hasSize(10);
    }

    @Test
    public void 질문을_상세조회한다() {
        Question question = qnAService.findById(1L);
        softly.assertThat(question.getQuestionBody())
            .isEqualTo(new QuestionBody("This is title1", "This is contents1"));
    }

    @Test(expected = NotFoundException.class)
    public void 없는_질문이면_예외가_발생한다() {
        qnAService.findById(100L);
    }

    @Test
    public void 질문을_수정한다() {
        User writer = UserTest.newUser(1L);
        QuestionBody newQuestionBody = new QuestionBody("This is updated title", "This is updated contents");
        Question question = qnAService.updateQuestion(1L, writer, newQuestionBody);

        softly.assertThat(question.getQuestionBody()).isEqualTo(newQuestionBody);
    }

    @Test
    public void 질문을_삭제한다() {
        User writer = UserTest.newUser(1L);
        qnAService.deleteQuestion(1L, writer);

        Question question = questionRepository.findById(1L).get();
        softly.assertThat(question.isDeleted()).isTrue();
    }

    /*
    @Test(expected = NotFoundException.class)
    public void 없는_질문이면_예외가_발생한다() {
        qnAService.findById(1L);
    }

    @Test
    public void 답변을_등록한다() {
        User user = new User("myId", "myPassword", "myName", "myEmail");
        Question question = new Question("This is title", "This is contents");
        question.writeBy(user);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        Answer answer = qnAService.addAnswer(user, question.getId(), "This is answer");

        softly.assertThat(answer.getContents()).isEqualTo("This is answer");
    }
    */
}
