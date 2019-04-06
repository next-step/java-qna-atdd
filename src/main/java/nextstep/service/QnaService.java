package nextstep.service;

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

    public Question show(long id) {
        return findById(id).orElseThrow(UnAuthorizedException::new);
    }

    public Question findQuestion(long id, User loginUser) {
        return findById(id)
                .filter(question -> question != null)
                .filter(question -> question.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        return findById(id)
                .filter(question -> question != null)
                .map(question -> question.modify(loginUser, updatedQuestion))
                .orElseThrow(UnAuthorizedException::new);
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long id) {
        return findById(id)
                .filter(question -> question != null)
                .map(question -> question.delete(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long id, String contents) {
        // TODO 답변 추가 기능 구현
        return null;
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현
        return null;
    }
}
