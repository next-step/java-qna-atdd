package nextstep.service;

import com.google.common.collect.ImmutableList;
import nextstep.UnAuthenticationException;
import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import nextstep.security.LoginChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final DeleteHistoryService deleteHistoryService;

    public QnaService(QuestionRepository questionRepository, AnswerRepository answerRepository,
                      DeleteHistoryService deleteHistoryService) {

        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.deleteHistoryService = deleteHistoryService;
    }

    @Transactional
    public Question create(User loginUser, Question question) throws UnAuthenticationException {
        LoginChecker.check(loginUser);
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    public Question findNotDeletedQuestionById(Long id) {
        return questionRepository.findByIdAndDeleted(id, false)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) throws UnAuthenticationException {
        LoginChecker.check(loginUser);

        Question findedQuestion = findNotDeletedQuestionById(id);

        return findedQuestion.update(loginUser, updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, Long questionId) throws UnAuthenticationException {
        LoginChecker.check(loginUser);

        Question findedQuestion = findNotDeletedQuestionById(questionId);

        findedQuestion.delete(loginUser);

        DeleteHistory deleteHistory = new DeleteHistory(ContentType.QUESTION, questionId, loginUser, LocalDateTime.now());
        deleteHistoryService.saveAll(ImmutableList.of(deleteHistory));
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        // TODO 답변 추가 기능 구현
        return null;
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현 
        return null;
    }
}
