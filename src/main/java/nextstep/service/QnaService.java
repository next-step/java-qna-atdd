package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryRepository deleteHistoryRepository;

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Question update(User loginUser, long questionId, Question updatedQuestion) {
        Question question = findById(questionId);
        return question.update(loginUser, updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = findById(questionId);
        question.delete(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer findAnswer(long answerId) {
        return answerRepository.findById(answerId)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, Answer answer) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(EntityNotFoundException::new);

        answer = new Answer(loginUser, answer.getContents());
        question.addAnswer(answer);
        return answer;
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        Answer answer = answerRepository.findById(id).get();
        deleteHistoryRepository.save(answer.delete(loginUser));
    }
}
