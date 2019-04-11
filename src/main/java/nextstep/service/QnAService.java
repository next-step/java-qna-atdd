package nextstep.service;

import nextstep.NotFoundException;
import nextstep.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("qnaService")
public class QnAService {
    private static final Logger log = LoggerFactory.getLogger(QnAService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    @Transactional
    public Question createQuestion(User writer, QuestionBody questionBody) {
        Question question = new Question(writer, questionBody);

        return questionRepository.save(question);
    }

    @Transactional(readOnly = true)
    public List<Question> findQuestions() {
        return questionRepository.findByDeletedFalse();
    }

    @Transactional(readOnly = true)
    public Question findQuestionById(Long id) {
        return questionRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Question updateQuestion(Long id, User loginUser, QuestionBody newQuestionBody) {
        Question question = findQuestionById(id);
        question.update(loginUser, newQuestionBody);

        return question;
    }

    @Transactional
    public List<DeleteHistory> deleteQuestion(Long id, User writer) {
        Question question = findQuestionById(id);
        List<DeleteHistory> histories = question.delete(writer);

        deleteHistoryService.saveAll(histories);
        return histories;
    }

    @Transactional
    public Answer addAnswer(User writer, Long questionId, String contents) {
        Question question = findQuestionById(questionId);

        Answer answer = new Answer(writer, question, contents);
        question.addAnswer(answer);

        return answer;
    }

    @Transactional(readOnly = true)
    public List<Answer> findAnswers(Long questionId) {
        Question question = findQuestionById(questionId);

        return question.getAnswers();
    }

    @Transactional(readOnly = true)
    public Answer findAnswer(Long answerId) {
        return answerRepository.findByIdAndDeletedFalse(answerId)
            .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public DeleteHistory deleteAnswer(User writer, Long id) {
        Answer answer = findAnswer(id);

        return answer.delete(writer);
    }
}
