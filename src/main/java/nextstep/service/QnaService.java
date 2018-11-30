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

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    @Transactional
    public void update(User loginUser, long id, Question updatedQuestion) {
        // TODO 수정 기능 구현
        Question update = findById(id).orElseThrow(UnAuthorizedException::new);
        update.update(loginUser,updatedQuestion);

    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        // TODO 삭제 기능 구현
        Question deleted = findById(questionId).orElseThrow(UnAuthorizedException::new);
        deleted.deleted(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        // TODO 답변 추가 기능 구현
        Question usingQuestion = findById(questionId).orElseThrow(IllegalArgumentException::new);
        Answer answer = new Answer(loginUser,contents);
        usingQuestion.addAnswer(answer);

        return answerRepository.save(answer);
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현
        Answer deleteAnswer = answerRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        System.out.println("서비스에서 삭제답변 확인" + deleteAnswer.getContents());
        deleteAnswer.delete(loginUser);
        return deleteAnswer;
    }

    public Answer showAnswer(long answer_id) {
        return answerRepository.findById(answer_id).orElseThrow(IllegalArgumentException::new);
    }

    public void update(User loginUser, long id) {
    }
}
