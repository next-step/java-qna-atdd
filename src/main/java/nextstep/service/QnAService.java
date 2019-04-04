package nextstep.service;

import nextstep.domain.*;
import nextstep.web.exception.ForbiddenException;
import nextstep.web.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("qnaService")
public class QnAService {
    private static final Logger log = LoggerFactory.getLogger(QnAService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    @Transactional(readOnly = true)
    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Question findById(long id) {
        return questionRepository.findById(id)
            .filter(q -> !q.isDeleted())
            .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Question createQuestion(User writer, QuestionBody questionBody) {
        Question question = new Question(writer, questionBody);

        return questionRepository.save(question);
    }

    @Transactional
    public Question updateQuestion(Long id, User loginUser, QuestionBody newQuestionBody) {
        Question question = findById(id);
        question.update(loginUser, newQuestionBody);

        return question;
    }

    @Transactional
    public void deleteQuestion(Long id, User writer) {
        Question question = findById(id);
        question.delete(writer);
    }

    @Transactional(readOnly = true)
    public Page<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Question findByOwner(User loginUser, long id) {
        Question question = findById(id);

        if (!question.isOwner(loginUser)) {
            throw new ForbiddenException();
        }

        return question;
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = findById(questionId);
        Answer answer = new Answer(loginUser, question, contents);

        return answerRepository.save(answer);
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현 
        return null;
    }
}
