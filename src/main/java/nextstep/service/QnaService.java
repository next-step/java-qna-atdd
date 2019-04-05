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
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) throws UnAuthenticationException {
        Question question = questionRepository.findById(id).orElseThrow(NullPointerException::new);
        question.update(loginUser, updatedQuestion);
        return question;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = questionRepository.findById(questionId).orElseThrow(EntityNotFoundException::new);
        if(!question.isOwner(loginUser)) {
            throw new CannotDeleteException("This Question is Not Yours!");
        }
        question.deleteQuestion();
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = findById(questionId).orElseThrow(IllegalArgumentException::new);
        Answer answer = new Answer(loginUser, contents);
        question.addAnswer(answer);
        questionRepository.save(question);
        return answer;
    }

    public void deleteAnswer(User loginUser, long id) throws EntityNotFoundException {
        Answer answer = answerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(answer.isOwner(loginUser)) {
            answerRepository.delete(answer);
        }
    }

    @Transactional
    public Answer updateAnswer(User loginUser, Long id, String contents) throws Exception {
        Answer answer = answerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        answer.update(loginUser, contents);
        return answer;
    }
}
