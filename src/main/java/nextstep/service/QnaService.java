package nextstep.service;

import nextstep.AnswerNotFoundException;
import nextstep.CannotDeleteException;
import nextstep.QuestionNotFoundException;
import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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
        Question question = findById(id).orElseThrow(UnAuthorizedException::new);
        question.update(loginUser, updatedQuestion);
        return question;
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = findById(questionId).orElseThrow(UnAuthorizedException::new);
        question.delete(loginUser);
        return question;
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long id, String contents) {
        Question question = findById(id).orElseThrow(QuestionNotFoundException::new);
        Answer answer = new Answer(id, loginUser, question, contents);
        question.addAnswer(answer);
        return answer;
    }

    public List<Question> findAllQuestions() {
        Iterable<Question> questionIterable = findAll();
        List<Question> questions = new ArrayList<>();
        questionIterable.iterator()
                .forEachRemaining(questions::add);

        return questions;
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        Answer answer = answerRepository.findById(id).orElseThrow(AnswerNotFoundException::new);
        return answer.delete(loginUser);
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long id, String contents) {
        Answer answer = answerRepository.findById(id).orElseThrow(AnswerNotFoundException::new);
        answer.update(loginUser, contents);
        return answer;
    }

    public Answer findAnswerById(long id) {
        return answerRepository.findById(id).orElseThrow(AnswerNotFoundException::new);
    }
}
