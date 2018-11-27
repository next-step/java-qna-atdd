package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.UnAuthenticationException;
import nextstep.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CancellationException;

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
    public Question update(User loginUser, long id, Question updatedQuestion) throws UnAuthenticationException {
        Question question = questionRepository.findById(id)
                .filter(q -> q.isOwner(loginUser))
                .orElseThrow(UnAuthenticationException::new);

        updatedQuestion.writeBy(loginUser);
        return questionRepository.save(updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = questionRepository.findById(questionId)
                .filter(q -> q.isOwner(loginUser))
                .filter(q -> !q.isDeleted())
                .orElseThrow(() -> {
                    return new CannotDeleteException("삭제가 불가능합니다.");
                });

        questionRepository.deleteById(questionId);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(NoSuchElementException::new);

        Answer answer = new Answer(loginUser, question, contents);
        question.addAnswer(answer);
        return answerRepository.save(answer);
    }

    public void deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        // TODO 답변 삭제 기능 구현
        Answer answer = answerRepository.findById(id)
                .filter(a -> a.isOwner(loginUser))
                .filter(a -> !a.isDeleted())
                .orElseThrow(() -> {
                    return new CannotDeleteException("삭제가 불가능합니다.");
        });

        answerRepository.delete(answer);
    }
}
