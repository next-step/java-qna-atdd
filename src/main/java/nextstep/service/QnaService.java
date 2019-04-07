package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);
    private final QuestionRepository questionRepository;
    private final DeleteHistoryRepository deleteHistoryRepository;
    private final AnswerRepository answerRepository;

    private final DeleteHistoryService deleteHistoryService;

    public QnaService(DeleteHistoryService deleteHistoryService,
                      AnswerRepository answerRepository,
                      DeleteHistoryRepository deleteHistoryRepository,
                      QuestionRepository questionRepository) {

        this.deleteHistoryService = deleteHistoryService;
        this.answerRepository = answerRepository;
        this.deleteHistoryRepository = deleteHistoryRepository;
        this.questionRepository = questionRepository;
    }

    public Question createQuestion(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);

        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return questionRepository.findById(id)
            .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question original = findById(id);
        original.update(loginUser, updatedQuestion);

        return original;
    }

    @Transactional
    public List<DeleteHistory> deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question deletedQuestion = findById(questionId);

        List<DeleteHistory> deleteHistories = deletedQuestion.delete(loginUser);
        deleteHistoryService.saveAll(deleteHistories);

        return deleteHistories;
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, Answer answer) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(EntityNotFoundException::new);
        answer.writeBy(loginUser);
        question.addAnswer(answer);

        return answerRepository.save(answer);
    }

    @Transactional
    public Answer findAnswer(long questionId, long answerId) {
        return findById(questionId).getAnswer(answerId);
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long questionId, long answerId, Answer targetAnswer) {
        Answer original = findAnswer(questionId, answerId);
        return original.update(loginUser, targetAnswer.getContents());
    }

    @Transactional
    public DeleteHistory deleteAnswer(User loginUser, long questionId, long answerId) {
        Answer deletedAnswer = findAnswer(questionId, answerId);
        DeleteHistory deleteHistory = deletedAnswer.delete(loginUser);
        deleteHistoryRepository.save(deleteHistory);

        return deleteHistory;
    }
}
