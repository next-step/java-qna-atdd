package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.domain.*;
import nextstep.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        return questionRepository.save(question);
    }

    public List<Question> findQuestions() {
        return questionRepository.findAllByDeleted(false);
    }

    public Question findQuestion(long id) {
        return questionRepository.findById(id).get();
    }

    @Transactional
    public Question updateQuestion(User loginUser, long id, QuestionDto updatedQuestionDto) throws CannotUpdateException {
        Question question = questionRepository.findById(id).get();
        question.update(loginUser, updatedQuestionDto);
        return question;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long id) throws CannotDeleteException {
        Question question = questionRepository.findById(id).get();
        deleteHistoryService.saveAll(question.delete(loginUser));
    }

    @Transactional
    public Answer createAnswer(User loginUser, long questionId, String contents) {
        Question question = questionRepository.findById(questionId).get();
        Answer answer = new Answer(loginUser, contents);

        question.addAnswer(answer);

        return answer;
    }

    @Transactional(readOnly = true)
    public List<Answer> findAnswers(long questionId) {
        Question question = questionRepository.findById(questionId).get();

        return question.getAnswers();
    }

    public Answer findAnswer(long id) {
        return answerRepository.findById(id).get();
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long id, String contents) throws CannotUpdateException {
        Answer answer = answerRepository.findById(id).get();
        answer.update(loginUser, contents);
        return answer;
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        Answer answer = answerRepository.findById(id).get();
        deleteHistoryService.save(answer.delete(loginUser));
    }
}
