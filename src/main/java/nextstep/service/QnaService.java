package nextstep.service;

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

    @Transactional
    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
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
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        // TODO 삭제 기능 구현
        if(!findById(questionId).get().isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        questionRepository.deleteById(questionId);
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
        Question question = questionRepository.findById(questionId).orElseThrow(IllegalArgumentException::new);
        question.addAnswer(answer);
        return answer;
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long id, Answer updatedAnswer) {
        // TODO 답변 수정 기능 구현
        Answer answer = answerRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        if(!answer.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        answer.update(loginUser, updatedAnswer);
        return answer;
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        // TODO 답변 삭제 기능 구현
        Answer answer = answerRepository.findById(id).get();
        if(!answer.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        return answer.delete(loginUser);
    }
}