package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.CannotFoundException;
import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service("qnaService")
@Transactional
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question createQuestion(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    public Question updateQuestion(User loginUser, long id, Question updatedQuestion) {
        Question question = findByIdAndDeletedFalse(loginUser ,id);
        updatedQuestion.writeBy(loginUser);
        question.update(updatedQuestion);;
        return question;
    }

    public Question findByIdAndDeletedFalse(User loginUser, long questionId) {
        return findById(questionId)
                .filter(question -> !question.isDeleted())
                .filter(question -> question.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }

    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question target = findById(questionId).orElseThrow(CannotDeleteException::new);
        List<DeleteHistory> deleteHistorys = target.delete(loginUser);
        deleteHistoryService.saveAll(deleteHistorys);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    Question findQuestion(long questionId) throws CannotFoundException {
        return questionRepository.findByIdAndDeletedFalse(questionId).orElseThrow(CannotFoundException::new);
    }

    public void addAnswer(User loginUser, long questionId, String contents) throws CannotFoundException {
        Question question = findQuestion(questionId);
        question.addAnswer(new Answer(loginUser, contents));
    }

    public Answer deleteAnswer(User loginUser, long questionId, long answerId) throws CannotDeleteException, CannotFoundException {
        findQuestion(questionId);
        Answer answer = answerRepository.findById(answerId).orElseThrow(CannotFoundException::new);
        DeleteHistory deleteHistory = answer.delete(loginUser);
        deleteHistoryService.saveAll(Arrays.asList(deleteHistory));
        return answer;
    }
}
