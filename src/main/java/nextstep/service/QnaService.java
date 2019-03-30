package nextstep.service;

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

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        questionRepository.findById(id)
                .filter(question -> question.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);

        return questionRepository.save(updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) {
        Question question = questionRepository.findById(questionId)
                .filter(q -> q.isOwner(loginUser))
                .orElseThrow(EntityNotFoundException::new);
        questionRepository.delete(question);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(EntityNotFoundException::new);
        Answer answer = new Answer(loginUser, contents);
        answer.toQuestion(question);
        return answerRepository.save(answer);
    }

    public Answer deleteAnswer(User loginUser, long id) {
        Answer answer = answerRepository.findById(id)
                .filter(a -> a.isOwner(loginUser))
                .orElseThrow(EntityNotFoundException::new);
        answerRepository.delete(answer);
        return answer;
    }
}
