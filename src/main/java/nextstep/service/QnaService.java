package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.QuestionPermissionException;
import nextstep.domain.*;
import nextstep.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static java.util.Optional.of;

@Service("qnaService")
public class QnaService {

    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question findByUserAndId(final User user, final long id) {
        final Question question = questionRepository.findById(id)
                .orElseThrow(QnaService::resourceNotFoundException);
        return of(question).filter(q -> q.isOwner(user))
                .orElseThrow(QnaService::questionPermissionException);
    }

    public Question findById(final long id) {
        return questionRepository.findById(id)
                .orElseThrow(QnaService::resourceNotFoundException);
    }

    public List<Question> findAll(final Pageable pageable) {
        return questionRepository.findByDeleted(false, pageable).getContent();
    }

    @SuppressWarnings("UnusedReturnValue")
    public Question create(final User loginUser, final Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    @Transactional
    public Question update(final User loginUser, final long id, final Question updatedQuestion) {
        final Question question = findById(id);
        question.update(loginUser, updatedQuestion);
        return question;
    }

    @Transactional
    public void delete(final User loginUser, final long id) throws CannotDeleteException {
        findById(id).delete(loginUser);
    }

    @Transactional
    public Answer addAnswer(final User loginUser, final long questionId, final String contents) {
        final Answer answer = answerRepository.save(new Answer(loginUser, contents));
        final Question question = findById(questionId);
        question.addAnswer(answer);
        return answer;
    }

    @Transactional
    public Answer updateAnswer(final User loginUser, final Answer updateAnswer) {
        final Answer savedAnswer = answerRepository.findById(updateAnswer.getId())
                .orElseThrow(QnaService::resourceNotFoundException);
        savedAnswer.update(loginUser, updateAnswer);
        return savedAnswer;
    }

    @Transactional
    public Answer deleteAnswer(final User loginUser, final long id) {
        final Answer answer = answerRepository.findById(id)
                .orElseThrow(QnaService::resourceNotFoundException);
        answer.delete(loginUser);
        return answer;
    }

    private static ResourceNotFoundException resourceNotFoundException() {
        return new ResourceNotFoundException("Not found question");
    }

    private static QuestionPermissionException questionPermissionException() {
        return new QuestionPermissionException("You do not have permission to view questions.");
    }

}
