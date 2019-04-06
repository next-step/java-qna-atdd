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

@RunWith(SpringRunner.class)
@DataJpaTest
public class QnAServiceTest extends BaseTest {
    private User fixtureWriter;
    private Question fixtureQuestion;
    private Answer fixtureAnswer;

    @Autowired private QnAService qnaService;

    @Autowired private UserRepository userRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private AnswerRepository answerRepository;

    @Before
    public void setUp() {
        fixtureWriter = userRepository.save(
            new User("userId", "pass", "name", "javajigi@slipp.net"));
        fixtureQuestion = questionRepository.save(
            new Question(fixtureWriter, new QuestionBody("This is title", "This is contents")));
        fixtureAnswer = answerRepository.save(new Answer(fixtureWriter, fixtureQuestion, "fixtureAnswer"));
    }

    @Test
    public void 질문을_등록한다() {
        QuestionBody questionBody = new QuestionBody("This is title", "This is contents");

        Question question = qnaService.createQuestion(fixtureWriter, questionBody);

        softly.assertThat(question.getQuestionBody()).isEqualTo(questionBody);
    }

    @Test
    public void 질문_목록을_조회한다() {
        List<Question> list = qnaService.findQuestions();
        softly.assertThat(list).hasSize(1);
    }

    @Test
    public void 질문을_상세조회한다() {
        Long questionId = fixtureQuestion.getId();

        Question question = qnaService.findQuestionById(questionId);
        softly.assertThat(question.getQuestionBody())
            .isEqualTo(fixtureQuestion.getQuestionBody());
    }

    @Test(expected = NotFoundException.class)
    public void 없는_질문이면_예외가_발생한다() {
        qnaService.findQuestionById(0L); // auto_increment 는 1부터 시작하니까..
    }

    @Test
    public void 질문을_수정한다() {
        Long questionId = fixtureQuestion.getId();

        QuestionBody newQuestionBody = new QuestionBody("This is updated title", "This is updated contents");
        Question question = qnaService.updateQuestion(questionId, fixtureWriter, newQuestionBody);

        softly.assertThat(question.getQuestionBody()).isEqualTo(newQuestionBody);
    }

    @Test
    public void 질문을_삭제한다() {
        Long questionId = fixtureQuestion.getId();

        qnaService.deleteQuestion(questionId, fixtureWriter);

        Question question = questionRepository.findById(questionId).get();
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test
    public void 답변을_등록한다() {
        Long questionId = fixtureQuestion.getId();

        String contents = "This is fixtureAnswer";
        Answer answer = qnaService.addAnswer(fixtureWriter, questionId, contents);

        softly.assertThat(answer.getWriter()).isEqualTo(fixtureWriter);
        softly.assertThat(answer.getContents()).isEqualTo(contents);
    }

    @Test
    public void 답변목록을_조회한다() {
        Long questionId = fixtureQuestion.getId();
        List<Answer> answers = qnaService.findAnswers(questionId);

        softly.assertThat(answers).hasSize(1);
    }

    @Test
    public void 답변을_상세조회한다() {
        Long answerId = fixtureAnswer.getId();

        Answer answer = qnaService.findAnswer(answerId);

        softly.assertThat(answer.getContents()).isEqualTo(fixtureAnswer.getContents());
    }
}
