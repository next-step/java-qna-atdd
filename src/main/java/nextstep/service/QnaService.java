package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
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

    public Question findById(long id) {
        return questionRepository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question original = findById(id);
        original.update(loginUser, updatedQuestion);
        return original;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long id) throws CannotDeleteException {
        Question target = findById(id);
        target.delete(loginUser);
    }

    public Page<Question> findAll(Pageable pageable) {
        return questionRepository.findByDeleted(pageable, false);
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Answer answer = new Answer(loginUser, contents);
        findById(questionId).addAnswer(answer);
        return answerRepository.save(answer);
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long id, Answer updatedAnswer) {
        Answer original = findAnswerById(id);
        original.update(loginUser, updatedAnswer);
        return original;
    }

    @Transactional
    public void deleteAnswer(User loginUser, long questionId, long id) {
        Answer target = findAnswerById(questionId, id);
        target.delete(loginUser);
    }

    public Answer findAnswerById(long questionId, long id) {
        Optional.of(findById(questionId))
                .filter(question -> question.containsAnswer(id))
                .orElseThrow(EntityNotFoundException::new);

        return findAnswerById(id);
    }

    private Answer findAnswerById(long id) {
        return answerRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }
}