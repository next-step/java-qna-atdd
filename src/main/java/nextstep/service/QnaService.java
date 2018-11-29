package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    @Resource(name = "deleteQuestionPolicy")
    private DeletePolicy deleteQuestionPolicy;

    @Transactional
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
        Question foundQuestion = findById(questionId);
        saveDeleteHistories(foundQuestion.delete(loginUser, deleteQuestionPolicy));
    }

    private void saveDeleteHistories(DeleteHistories deleteHistories) throws CannotDeleteException {
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
        Answer answer = answerRepository.save(new Answer(loginUser, contents));
        findById(questionId).addAnswer(answer);
        return answer;
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        Answer foundAnswer = getAnswer(id);
        deleteHistoryService.save(foundAnswer.delete(loginUser));
        return foundAnswer;
    }

    private Answer getAnswer(long id) {
        return answerRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }
}
