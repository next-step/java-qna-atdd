package nextstep.service;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import nextstep.CannotDeleteException;
import nextstep.domain.Answer;
import nextstep.domain.AnswerRepository;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final DeleteHistoryService deleteHistoryService;

    public QnaService(DeleteHistoryService deleteHistoryService, AnswerRepository answerRepository,
                      QuestionRepository questionRepository) {
        this.deleteHistoryService = deleteHistoryService;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    public Question create(User loginUser, Question question) {
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
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question deletedQuestion = findById(questionId);
        deletedQuestion.delete(loginUser);
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

        return answer;
    }

    public Answer findAnswer(long questionId, long answerId) {
        Optional.of(findById(questionId))
                .filter(question -> question.containsAnswer(answerId))
                .orElseThrow(EntityNotFoundException::new);

        return answerRepository.findById(answerId)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long questionId, long answerId, Answer targetAnswer) {
        Answer original = findAnswer(questionId, answerId);
        return original.update(loginUser, targetAnswer.getContents());
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long questionId, long answerId) {
        Answer deletedAnswer = findAnswer(questionId, answerId);
        return deletedAnswer.delete(loginUser);
    }
}
