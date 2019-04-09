package nextstep.service;

import lombok.RequiredArgsConstructor;
import nextstep.domain.Answer;
import nextstep.domain.AnswerRepository;
import nextstep.domain.DeleteHistory;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.exception.CannotDeleteException;
import nextstep.exception.UnAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service("qnaService")
@RequiredArgsConstructor
public class QnaService {

    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final DeleteHistoryService deleteHistoryService;

    public Question createQuestion(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    @Transactional
    public Question updateQuestion(User loginUser, long id, Question updatedQuestion) {
        Question original = findQuestionById(id);
        return original.update(loginUser, updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question target = findQuestionById(questionId);
        List<DeleteHistory> deleteHistories = target.delete(loginUser);
        deleteHistoryService.saveAll(deleteHistories);
    }

    public Question findQuestionById(long id) {
        return questionRepository.findByIdAndDeletedFalse(id).orElseThrow(EntityNotFoundException::new);
    }

    public Question findQuestionById(User loginUser, long id) {
        Question question = findQuestionById(id);

        if (!question.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        return question;
    }

    public List<Question> findAll(int page, int size) {
        return questionRepository.findAllByDeleted(false, PageRequest.of(page - 1, size));
    }

    public Answer createAnswer(User loginUser, long questionId, String contents) {
        Answer answer = new Answer(loginUser, contents);
        findQuestionById(questionId).addAnswer(answer);
        return answerRepository.save(answer);
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long id, Answer updatedAnswer) {
        Answer original = findAnswerById(id);
        return original.update(loginUser, updatedAnswer);
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) {
        Answer target = findAnswerById(id);
        DeleteHistory deleteHistory = target.delete(loginUser);
        deleteHistoryService.save(deleteHistory);
    }

    public Answer findAnswerById(long id) {
        return answerRepository.findByIdAndDeletedFalse(id).orElseThrow(EntityNotFoundException::new);
    }
}
