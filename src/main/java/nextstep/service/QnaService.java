package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
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
        return questionRepository.findByIdAndDeleted(id, false);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        return findById(id).map((question) -> {
            question.update(loginUser, updatedQuestion);
            return question;
        }).get();
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        List<DeleteHistory> deleteHistories = findById(questionId)
            .filter(question -> !question.isDeleted())
            .orElseThrow(() -> new CannotDeleteException("이미 삭제되었거나 삭제할 질문이 존재하지 않습니다."))
            .delete(loginUser);
        deleteHistoryService.saveAll(deleteHistories);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {

        Answer answer = new Answer(loginUser, contents);
        answer.toQuestion(findById(questionId).get());
        Answer a =  answerRepository.save(answer);
        return a;
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long answerId, Answer updateAnswer) {
        return findAnswerById(answerId).map((answer) -> {
            answer.update(loginUser, updateAnswer);
            return answer;
        }).get();
    }

    @Transactional
    public void deleteAnswer(User loginUser, long answerId) throws CannotDeleteException {
        DeleteHistory deleteHistory = findAnswerById(answerId)
            .filter(answer -> !answer.isDeleted())
            .orElseThrow(() -> new CannotDeleteException("이미 삭제되었거나 삭제할 답변이 존재하지 않습니다."))
            .delete(loginUser);
        deleteHistoryService.saveAll(Arrays.asList(deleteHistory));
    }

    public Optional<Answer> findAnswerById(long id) {
        return answerRepository.findByIdAndDeleted(id, false);
    }
}
