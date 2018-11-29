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

@Service("answerService")
public class AnswerService {
    private static final Logger log = LoggerFactory.getLogger(AnswerService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        // TODO 답변 추가 기능 구현
        Question question = questionRepository.findById(questionId).orElseThrow(UnAuthorizedException::new);
        Answer answer = new Answer(loginUser, contents);
        question.addAnswer(answer);
        answerRepository.save(answer);
        return answer;
    }

    public Answer deleteAnswer(User loginUser, long answerId) throws CannotDeleteException {
        // TODO 답변 삭제 기능 구현
        Answer answer = answerRepository.findById(answerId).orElseThrow(EntityNotFoundException::new);
        DeleteHistory deleteHistory = answer.delete(loginUser);
        deleteHistoryService.save(deleteHistory);
        return answer;
    }

    public Answer updateAnswer(User loginUser, long answerId, String contents) {
        return answerRepository.findById(answerId).orElseThrow(EntityNotFoundException::new).update(loginUser, contents);
    }

    public Question findByUserId(User loginUser, long questionId) {
        return questionRepository.findById(questionId).filter(question -> question.isOwner(loginUser)).orElseThrow(UnAuthorizedException::new);
    }

    public List<Answer> findByQuestionIdAll(long questionId) {
        return answerRepository.findAllByQuestionId(questionId);
    }

    public Answer findByAnswerId(long id) {
        return answerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
