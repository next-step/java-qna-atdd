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
import javax.persistence.EntityNotFoundException;
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

    private Optional<Question> findQuestionById(long id) {
        return questionRepository.findById(id);
    }

    public Question findById(long id) {
        return findQuestionById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Question update(User loginUser, long questionId, Question updatedQuestion) {
        Question question = findById(questionId);
        question.update(loginUser, updatedQuestion);

        return question;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question targetQuestion = findById(questionId);
        targetQuestion.delete(loginUser);
    }

    public Question findByIdAndOwner(long id, User loginUser) {
        return findQuestionById(id)
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

    public Answer findAnswerByIdAndQuestion(long answerId, long questionId) {
        return answerRepository.findByIdAndQuestionId(answerId, questionId)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Answer answer = new Answer(loginUser, contents);

        Question question = findById(questionId);
        question.addAnswer(answer);

        return answer;
    }

    @Transactional
    public void deleteAnswer(User loginUser, long questionId, long answerId) throws CannotDeleteException {
        Answer answer = findAnswerByIdAndQuestion(answerId, questionId);
        answer.delete(loginUser);
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long questionId, long answerId, Answer modifiedAnswer) {
        Answer answer = findAnswerByIdAndQuestion(answerId, questionId);
        answer.update(loginUser, modifiedAnswer);

        return answer;
    }
}
