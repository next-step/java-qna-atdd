package nextstep.service;

import javax.persistence.EntityNotFoundException;
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

    public Question findById(User loginUser, long id) {

        Optional<Question> optionalQuestion = findById(id);
        if(!optionalQuestion.isPresent()) {
            throw new EntityNotFoundException();
        }

        return optionalQuestion
            .filter(question -> question.isOwner(loginUser))
            .orElseThrow(UnAuthorizedException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {

        Question original = findById(id).orElseThrow(EntityNotFoundException::new);
        original.update(loginUser, updatedQuestion);
        return original;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long id) throws CannotDeleteException {

        Question target = findById(id).orElseThrow(EntityNotFoundException::new);
        target.delete(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {

        Answer answer = new Answer(loginUser, contents);
        findById(questionId)
            .orElseThrow(EntityNotFoundException::new)
            .addAnswer(answer);

        return answerRepository.save(answer);
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현
        return null;
    }

    public Answer findAnswerById(long questionId, long id) {

        Question question = findById(questionId)
            .orElseThrow(EntityNotFoundException::new);

        return answerRepository.findById(id)
            .filter(answer -> answer.isQuestion(question))
            .orElseThrow(EntityNotFoundException::new);
    }
}
