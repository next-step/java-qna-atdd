package nextstep.service;

import lombok.RequiredArgsConstructor;
import nextstep.domain.Answer;
import nextstep.domain.AnswerRepository;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.exception.UnAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service("qnaService")
@RequiredArgsConstructor
public class QnaService {

    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final DeleteHistoryService deleteHistoryService;

    public Question createQuestion(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    @Transactional
    public Question updateQuestion(User loginUser, long id, Question updatedQuestion) {
        Question original = findQuestionById(id);
        return original.update(loginUser, updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) {
        Question target = findQuestionById(questionId);
        target.delete(loginUser);
    }

    public Question findQuestionById(long id) {
        return questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Question findQuestionById(User loginUser, long id) {
        Question question = findQuestionById(id);

        if (!question.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        return question;
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(int page, int size) {
        return questionRepository.findAllByDeleted(false, PageRequest.of(page - 1, size));
    }

    public Answer createAnswer(User loginUser, long questionId, String contents) {
        Answer answer = new Answer(loginUser, contents);
        findQuestionById(questionId).addAnswer(answer);
        return answerRepository.save(answer);
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long id, Answer updatedAnswer) {
        Answer original = findAnswerById(id);
        original.update(loginUser, updatedAnswer);
        return original;
    }

    @Transactional
    public void deleteAnswer(User loginUser, long questionId, long id) {
        Answer target = findAnswerById(questionId, id);
        target.delete(loginUser);
    }

    private Answer findAnswerById(long id) {
        return answerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Answer findAnswerById(long questionId, long id) {
        if (!findQuestionById(questionId).isContainsAnswer(id)) {
            throw new EntityNotFoundException();
        }
        return findAnswerById(id);
    }
}
