package nextstep.service;

import com.google.common.collect.ImmutableList;
import nextstep.CannotDeleteException;
import nextstep.UnAuthenticationException;
import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    @Transactional
    public Question create(User loginUser, Question question) {
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
    public Question update(User loginUser, long id, Question updatedQuestion) {
        // TODO 수정 기능 구현
        log.debug("question : {}", updatedQuestion);
        if(!findById(id).get().isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        return questionRepository.save(updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) {
        // TODO 삭제 기능 구현
        Question question = findNotDeletedQuestionById(questionId);

        if(!question.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        List<DeleteHistory> deleteHistories = question.delete(loginUser);
        deleteHistoryService.saveAll(deleteHistories);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Optional<Answer> findByAnswerId(long id) {
        return answerRepository.findById(id);
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, Answer answer) throws UnAuthenticationException {
        // TODO 답변 추가 기능 구현
        if(loginUser.isGuestUser()) {
            throw new UnAuthenticationException();
        }
        answer.writeBy(loginUser);
        Question question = findNotDeletedQuestionById(questionId);
        question.addAnswer(answer);
        return answer;
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long id, Answer updatedAnswer) {
        // TODO 답변 수정 기능 구현
        Answer answer = answerRepository.findAnswerByIdAndDeleted(id ,false).orElseThrow(IllegalArgumentException::new);
        if(!answer.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        answer.update(loginUser, updatedAnswer);
        return answer;
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현
        Answer answer = answerRepository.findAnswerByIdAndDeleted(id, false).orElseThrow(EntityNotFoundException::new);

        if(!answer.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        answer.delete(loginUser);
        deleteHistoryService.saveAll(ImmutableList.of(new DeleteHistory(ContentType.ANSWER, id, loginUser, LocalDateTime.now())));

        return answer;
    }
}