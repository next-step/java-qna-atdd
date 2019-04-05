package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import nextstep.domain.*;
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
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    @Transactional
    public Question update(User loginUser, long questionId, Question updatedQuestion) {
        Question originalQuestion = findById(questionId)
                .orElseThrow(IllegalArgumentException::new);
        originalQuestion.update(loginUser, updatedQuestion);

        return originalQuestion;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question targetQuestion = findById(questionId)
                .orElseThrow(IllegalArgumentException::new);

        targetQuestion.delete(loginUser);
    }

    public Question findByIdAndOwner(long id, User loginUser) {
        return findById(id)
                .filter(question -> question.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public List<Question> findUsedAll(Pageable pageable) {
        return questionRepository.findAllByDeleted(false, pageable);
    }

    public Answer findAnswerById(long answerId) {
        return answerRepository.findById(answerId)
                .orElseThrow(IllegalArgumentException::new);
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = findById(questionId)
                .orElseThrow(IllegalArgumentException::new);

        Answer answer = new Answer(loginUser, contents);
        question.addAnswer(answer);

        return answerRepository.save(answer);
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) {
        Answer answer = findAnswerById(id);
        return answer.delete(loginUser);
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long id, Answer modifiedAnswer) {
        Answer answer = findAnswerById(id);
        return answer.update(loginUser, modifiedAnswer);
    }
}
