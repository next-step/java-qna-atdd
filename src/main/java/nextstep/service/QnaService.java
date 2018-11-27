package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.NotFoundException;
import nextstep.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
        log.debug("loginUser : {}", loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Question findOne(long id) {
        return questionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Question update(User loginUser, long questionId, Question updatedQuestion) {
        Question original = findOne(questionId);
        original.update(loginUser, updatedQuestion);
        return original;
    }

    @Transactional
    public void delete(User loginUser, long questionId) throws CannotDeleteException {
        Question original = findOne(questionId);
        original.delete(loginUser);
    }

    public Answer createAnswer(User loginUser, long questionId, Answer answer) {
        Question original = findOne(questionId);
        answer.writeBy(loginUser);
        original.addAnswer(answer);
        return answerRepository.save(answer);
    }

    public Answer findAnswer(long id) {
        return answerRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long questionId, long answerId, Answer updatedAnswer) {
        Question original = findOne(questionId);
        Answer answer = original.findAnswer(answerId);
        answer.update(loginUser, updatedAnswer);
        return answer;
    }

    @Transactional
    public void deleteAnswer(User loginUser, long questionId, long answerId) throws CannotDeleteException {
        Question original = findOne(questionId);
        Answer answer = original.findAnswer(answerId);
        answer.delete(loginUser);
    }
}
