package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.NotFoundException;
import nextstep.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Transactional
    public Question createQuestion(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    @Transactional(readOnly = true)
    public List<Question> findQuestions() {
        return questionRepository.findByDeletedFalse();
    }

    @Transactional(readOnly = true)
    public Question findQuestionById(Long id) {
        return questionRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Question updateQuestion(User loginUser, long id, Question updatedQuestion) {
        // TODO 수정 기능 구현
        return null;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        // TODO 삭제 기능 구현
    }

    @Transactional
    public Answer addAnswer(User writer, Long questionId, String contents) {
        Question question = findQuestionById(questionId);

        Answer answer = new Answer(writer, question, contents);
        question.addAnswer(answer);

        return answer;
    }

    @Transactional(readOnly = true)
    public List<Answer> findAnswers(Long questionId) {
        Question question = findQuestionById(questionId);

        return question.getAnswers();
    }

    @Transactional(readOnly = true)
    public Answer findAnswer(Long answerId) {
        return answerRepository.findByIdAndDeletedFalse(answerId)
            .orElseThrow(NotFoundException::new);
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        // TODO 답변 추가 기능 구현
        return null;
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현
        return null;
    }
}
