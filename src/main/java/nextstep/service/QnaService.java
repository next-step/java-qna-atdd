package nextstep.service;

import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import nextstep.dto.QuestionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import java.util.List;

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

    public Question findById(long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 질문글을 찾을 수 없습니다."));
    }

    public Answer findAnswerById(long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 답변을 찾을 수 없습니다."));
    }

    public Question findQuestionWithAnswer(long id) {
        return questionRepository.findQuestionWithAnswerById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 질문글을 찾을 수 없습니다."));
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        checkQuestionOwner(id, loginUser);
        Question origin = findById(id);
        origin.update(origin, updatedQuestion);
        return origin;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) {
        checkQuestionOwner(questionId, loginUser);
        Question question = findById(questionId);
        questionRepository.delete(question);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = findById(questionId);
        Answer answer = new Answer(loginUser, contents);
        question.addAnswer(answer);
        return answerRepository.save(answer);
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) {
        checkAnswerOwner(id, loginUser);
        Answer answer = findAnswerById(id);
        answerRepository.delete(answer);
        return answer;
    }

    public QuestionDTO findQuestionAndAnswerById(long questionId) {
        Question question = findQuestionWithAnswer(questionId);
        int answerSize = question.getAnswers().size();
        return new QuestionDTO(question, answerSize);
    }

    private void checkQuestionOwner(long questionId, User loginUser) {
        Question question = findById(questionId);
        if(!question.isOwner(loginUser)) {
            throw new UnAuthorizedException("해당 사용자가 작성한 글이 아닙니다.");
        }
    }

    private void checkAnswerOwner(long id, User loginUser) {
        Answer answer = findAnswerById(id);
        if(!answer.isOwner(loginUser)) {
            throw new UnAuthorizedException("해당 사용자가 작성한 답변이 아닙니다.");
        }
    }
}
