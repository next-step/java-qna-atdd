package nextstep.service;

import nextstep.UnAuthenticationException;
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
        return questionRepository.findById(id);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) throws UnAuthenticationException {
        Question question = findById(id).orElseThrow(UnAuthenticationException::new);
        question.update(loginUser, updatedQuestion);
        return question;
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long questionId) throws UnAuthenticationException {
        Question question = findById(questionId).orElseThrow(UnAuthenticationException::new);
        List<DeleteHistory> deleteHistories = question.delete(loginUser);
        deleteHistoryService.saveAll(deleteHistories);
        return question;
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) throws UnAuthenticationException {
        Answer answer = new Answer(loginUser, contents);
        findById(questionId).orElseThrow(UnAuthenticationException::new)
            .addAnswer(answer);
        return answerRepository.save(answer);
    }

    public Answer deleteAnswer(User loginUser, long id) throws UnAuthenticationException {
        Answer answer = answerRepository.findById(id).orElseThrow(UnAuthenticationException::new);
        DeleteHistory delete = answer.delete(loginUser);
        deleteHistoryService.saveAll(Arrays.asList(delete));
        return answer;
    }

    public Answer findByAnswerId(long id) throws UnAuthenticationException {
        return answerRepository.findById(id).orElseThrow(UnAuthenticationException::new);

    }
}
