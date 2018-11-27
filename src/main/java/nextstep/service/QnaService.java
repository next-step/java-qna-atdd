package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import nextstep.domain.*;
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

    public Question createQuestion(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    @Transactional
    public Question updateQuestion(User loginUser, long id, Question updatedQuestion) {
        // TODO 수정 기능 구현
        return questionRepository.findById(id).filter(question -> question.isOwner(loginUser)).orElseThrow(UnAuthorizedException::new).update(loginUser, updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        // TODO 삭제 기능 구현
        questionRepository.findById(questionId).filter(question -> question.isOwner(loginUser)).orElseThrow(UnAuthorizedException::new).delete(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        // TODO 답변 추가 기능 구현
        Question question = questionRepository.findById(questionId).orElseThrow(UnAuthorizedException::new);
        Answer answer = new Answer(loginUser, contents);
        question.addAnswer(answer);
        answerRepository.save(answer);
        return  answer;
    }

    public Answer deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        // TODO 답변 삭제 기능 구현 
        return answerRepository.findById(id).filter(answer -> answer.isOwner(loginUser)).orElseThrow(UnAuthorizedException::new).delete(loginUser);
    }

    public Answer updateAnswer(User loginUser, long id, String contents) {
        return answerRepository.findById(id).filter(answer -> answer.isOwner(loginUser)).orElseThrow(UnAuthorizedException::new).update(loginUser,contents);
    }

    public Question findByUserId(User loginUser, long id) {
        return questionRepository.findById(id).filter(question -> question.isOwner(loginUser)).orElseThrow(UnAuthorizedException::new);
    }

    public List<Answer> findByQuestionIdAll(long id) {

        return answerRepository.findAllByQuestionId(id);

    }
}
