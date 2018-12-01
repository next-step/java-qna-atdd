package nextstep.service;

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
    private DeleteHistoryService deleteHistoryService;

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question existing = findById(id);

        existing.update(loginUser, updatedQuestion);
        return existing;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) {
        Question existing = findById(questionId);

        existing.delete(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer findAnswer(long questionId, long answerId) {
        return answerRepository.findByQuestionAndIdAndDeletedIsFalse(findById(questionId), answerId)
            .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = findById(questionId);
        Answer answer = new Answer(loginUser, contents);

        question.addAnswer(answer);

        return answer;
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long questionId, long answerId, String contents) {
        Answer answer = findAnswer(questionId, answerId);

        answer.update(loginUser, contents);

        return answer;
    }

    @Transactional
    public void deleteAnswer(User loginUser, long questionId, long answerId) {
        Answer answer = findAnswer(questionId, answerId);

        answer.delete(loginUser);
    }
}
