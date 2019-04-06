package nextstep.service;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final DeleteHistoryService deleteHistoryService;
    private final UserRepository userRepository;

    private User getUser(User loginUser) {
        return userRepository.findByUserId(loginUser.getUserId()).orElseThrow(EntityNotFoundException::new);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    public Question create(User loginUser, Question question) {
        question.writeBy(getUser(loginUser));
        return questionRepository.save(question);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) throws UnAuthenticationException {
        Question question = findById(id).orElseThrow(EntityNotFoundException::new);
        question.update(loginUser, updatedQuestion);
        return question;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = findById(questionId).orElseThrow(EntityNotFoundException::new);
        if(question.isNotOwner(loginUser)) {
            throw new CannotDeleteException("This Question is Not Yours!");
        }
        question.deleteQuestion();
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public Optional<Answer> findByAnswerId(long id) {
        return answerRepository.findById(id);
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = findById(questionId).orElseThrow(IllegalArgumentException::new);
        Answer answer = Answer.builder().writer(getUser(loginUser)).question(question).contents(contents).build();
        answerRepository.save(answer);
        return answer;
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) throws Exception {
        Answer answer = findByAnswerId(id).orElseThrow(EntityNotFoundException::new);
        answer.delete(loginUser);
    }

    @Transactional
    public Answer updateAnswer(User loginUser, Long id, String contents) throws Exception {
        Answer answer = findByAnswerId(id).orElseThrow(EntityNotFoundException::new);
        answer.update(loginUser, contents);
        return answer;
    }
}
