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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@DataJpaTest
public class QnAServiceTest extends BaseTest {
    private User writer;
    private List<Question> questions;

    @Autowired private QnAService qnaService;

    @Autowired private UserRepository userRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private AnswerRepository answerRepository;

    @Before
    public void setUp() {
        writer = new User("userId", "pass", "name", "javajigi@slipp.net");
        questions = IntStream.rangeClosed(1, 10)
            .mapToObj(i -> new QuestionBody("This is title" + i, "This is contents" + i))
            .map(qb -> new Question(writer, qb))
            .collect(Collectors.toList());

        userRepository.save(writer);
        questionRepository.saveAll(questions);
    }

    @Test
    public void 질문을_등록한다() {
        QuestionBody questionBody = new QuestionBody("This is title", "This is contents");

        Question question = qnaService.createQuestion(writer, questionBody);

        softly.assertThat(question.getQuestionBody()).isEqualTo(questionBody);
    }

    @Test
    public void 질문_목록을_조회한다() {
        List<Question> list = qnaService.findQuestions();
        softly.assertThat(list).hasSize(10);
    }

    @Test
    public void 질문을_상세조회한다() {
        Long questionId = questions.get(0).getId();

        Question question = qnaService.findQuestionById(questionId);
        softly.assertThat(question.getQuestionBody())
            .isEqualTo(new QuestionBody("This is title1", "This is contents1"));
    }

    @Test(expected = NotFoundException.class)
    public void 없는_질문이면_예외가_발생한다() {
        qnaService.findQuestionById(0L); // auto_increment 는 1부터 시작하니까..
    }

    @Test
    public void 질문을_수정한다() {
        Long questionId = questions.get(0).getId();

        QuestionBody newQuestionBody = new QuestionBody("This is updated title", "This is updated contents");
        Question question = qnaService.updateQuestion(questionId, writer, newQuestionBody);

        softly.assertThat(question.getQuestionBody()).isEqualTo(newQuestionBody);
    }

    @Test
    public void 질문을_삭제한다() {
        Long questionId = questions.get(0).getId();

        qnaService.deleteQuestion(questionId, writer);

        Question question = questionRepository.findById(questionId).get();
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test
    public void 답변을_등록한다() {
        Long questionId = questions.get(0).getId();

        String contents = "This is answer";
        Answer answer = qnaService.addAnswer(writer, questionId, contents);

        softly.assertThat(answer.getWriter()).isEqualTo(writer);
        softly.assertThat(answer.getQuestion()).isEqualTo(questionRepository.findById(questionId).get());
        softly.assertThat(answer.getContents()).isEqualTo(contents);
    }

    @Test
    public void 답변을_조회한다() {
        answerRepository.save(new Answer(writer, questions.get(0), "answer1"));
        answerRepository.save(new Answer(writer, questions.get(0), "answer2"));

        Long questionId = questions.get(0).getId();
        List<Answer> answers = qnaService.findAnswers(questionId);

        softly.assertThat(answers).hasSize(2);
    }
}
