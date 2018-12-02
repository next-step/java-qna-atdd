package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.NotFoundExeption;
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

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Question findContentById(Long id) throws NotFoundExeption {
        return questionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundExeption());
    }

    @Transactional
    public void update(User user, long id, Question updatedQuestion) {
        findContentById(id).update(user, updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long id) {
        findContentById(id).delete(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) throws NotFoundExeption {
        Answer answer = new Answer(loginUser, contents);
        Question question = findContentById(questionId);
        question.addAnswer(answer);
        return answer;
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        findAnswerById(id).delete(loginUser);
    }

    @Transactional(readOnly = true)
    public Answer findAnswerById(Long id) throws NotFoundExeption {
        return answerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundExeption());
    }
}
