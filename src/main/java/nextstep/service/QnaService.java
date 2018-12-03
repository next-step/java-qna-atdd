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

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return questionRepository.findById(id)
                .orElseThrow(UnAuthorizedException::new);
    }

    public Answer findByIdAnswer(long id) {
        return answerRepository.findById(id)
                .orElseThrow(UnAuthorizedException::new);
    }

    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question origin = findById(id);
        return origin.update(loginUser, updatedQuestion);
    }

    public Question deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question origin = findById(questionId);
        origin.delete(loginUser);
        return origin;
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, Answer answer) {
        answer.writeBy(loginUser);
        Question question = findById(questionId);
        question.addAnswer(answer);
        return answer;
    }

    public Answer deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        Answer answer = findByIdAnswer(id);
        return answer.delete(loginUser);
    }
}
