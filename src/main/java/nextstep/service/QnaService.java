package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.NotFoundExeption;
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

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Question findContentById(Long id) throws NotFoundExeption {
        return questionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundExeption());
    }

    @Transactional(readOnly = true)
    public Question findContentById(User loginUser, Long id) throws UnAuthorizedException {
        return findById(id)
                .filter(question -> question.isOwner(loginUser))
                .filter(question -> !question.isDeleted())
                .orElseThrow(() -> new UnAuthorizedException("수정 권한이 없습니다."));
    }

    @Transactional
    public Question update(long id, Question updatedQuestion) throws NotFoundExeption {
        return findContentById(id).update(updatedQuestion);
    }

    @Transactional
    public boolean deleteQuestion(User loginUser, long id) throws CannotDeleteException {
        return findById(id)
                .filter(question -> question.isOwner(loginUser))
                .filter(question -> !question.isDeleted())
                .filter(question -> question.isNotExistAnswers())
                .map(question -> question.delete())
                .orElseThrow(() -> new CannotDeleteException("삭제 권한이 없거나 댓글이 존재 합니다."));
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) throws NotFoundExeption {
        Answer answer = new Answer(loginUser, contents);
        Question question = findContentById(questionId);
        question.addAnswer(answer);
        return answer;
    }

    @Transactional
    public boolean deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        return answerRepository.findById(id)
                .filter(answer -> answer.isOwner(loginUser))
                .map(answer -> answer.delete())
                .orElseThrow(() -> new CannotDeleteException("삭제 권한이 없습니다."));
    }
}
