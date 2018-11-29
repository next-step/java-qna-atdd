package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.domain.Answer;
import nextstep.domain.AnswerRepository;
import nextstep.domain.DeleteHistory;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
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

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) throws CannotUpdateException {
        Question question = findById(id).orElseThrow(IllegalArgumentException::new);
        question.update(updatedQuestion, loginUser);
        return question;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = questionRepository.findById(questionId).orElseThrow(IllegalArgumentException::new);
        List<DeleteHistory> deleteHistories = question.delete(loginUser);
        
        questionRepository.save(question);
        answerRepository.saveAll(question.getAnswers().getAnswers());
        deleteHistoryService.saveAll(deleteHistories);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Answer answer = new Answer(loginUser, contents);
        Question question = questionRepository.findById(questionId).orElseThrow(IllegalArgumentException::new);
        answer.toQuestion(question);
        log.debug("answer : {}", answer);
        Answer save = answerRepository.save(answer);

        return save;
    }

    @Transactional
    public void deleteAnswer(User loginUser, long questionId, long id) throws CannotDeleteException {
        Question question = questionRepository.findById(questionId).orElseThrow(IllegalArgumentException::new);
        Answer answer = answerRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        if(!answer.isOwner(loginUser)) {
            throw new CannotDeleteException("본인이 작성한 답변만 삭제할 수 있습니다.");
        }
        answerRepository.deleteById(id);
    }

    public Answer findAnswerById(long id) {
        return answerRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }
}
