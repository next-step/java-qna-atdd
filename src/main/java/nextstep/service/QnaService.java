package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return questionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("question이 존재하지 않음"));
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question original = findById(id);
        original.hasAuthority(loginUser);

        original.update(loginUser, updatedQuestion);

        return original;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = findById(questionId);
        List<DeleteHistory> deleteHistories = question.delete(loginUser);
        deleteHistoryService.saveAll(deleteHistories);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer findAnswerById(long questionId, long answerId) {
        return answerRepository.findById(answerId)
                .filter(a -> a.getQuestion().getId() == questionId)
                .orElseThrow(() -> new IllegalArgumentException("answer를 찾을 수 없음"));
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = findById(questionId);
        Answer answer = new Answer(loginUser, contents);
        question.addAnswer(answer);

        return answer;
    }

    @Transactional
    public void deleteAnswer(User loginUser, long questionId, long answerId) {
        Answer answer = findAnswerById(questionId, answerId);
        answer.hasAuthority(loginUser);

        answerRepository.delete(answer);
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long questionId, long answerId, String contents) {
        Answer answer = findAnswerById(questionId, answerId);
        answer.hasAuthority(loginUser);

        answer.setContents(contents);
        return answer;
    }
}
