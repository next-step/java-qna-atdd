package nextstep.service;

import java.util.List;
import javax.annotation.Resource;
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
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(EntityNotFoundException::new);

        Answer answer = new Answer(loginUser, contents);
        answer.toQuestion(question);

        return answer;
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) {
        Answer answer = answerRepository.findById(id)
            .orElseThrow(EntityNotFoundException::new);
        return answer.delete(loginUser);
    }
}
