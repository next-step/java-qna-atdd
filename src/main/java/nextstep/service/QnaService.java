package nextstep.service;

import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import nextstep.dto.AnswerDTO;
import nextstep.dto.QuestionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    private QuestionRepository questionRepository;

    private AnswerRepository answerRepository;

    private DeleteHistoryService deleteHistoryService;

    public QnaService(QuestionRepository questionRepository, AnswerRepository answerRepository, DeleteHistoryService deleteHistoryService) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.deleteHistoryService = deleteHistoryService;
    }

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
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

    @Query("select a from question join fetch a.anwers")
    private Question findQuestionWithAnswer(long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 질문글을 찾을 수 없습니다."));
    }

    @Transactional
    public QuestionDTO update(User loginUser, long id, Question updatedQuestion) {
        checkQuestionOwner(id, loginUser);
        Question origin = findById(id);
        origin.update(origin, updatedQuestion);
        return new QuestionDTO(origin);
    }

    @Transactional
    public QuestionDTO deleteQuestion(User loginUser, long questionId, LocalDateTime createDate) {
        checkQuestionOwner(questionId, loginUser);
        Question question = findById(questionId);
        return new QuestionDTO(question.delete(loginUser, createDate));
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public AnswerDTO addAnswer(User loginUser, long questionId, String contents) {
        Question question = findById(questionId);
        Answer answer = new Answer(loginUser, contents);
        question.addAnswer(answer);
        answer = answerRepository.save(answer);

        return new AnswerDTO(answer);
    }

    @Transactional
    public AnswerDTO deleteAnswer(User loginUser, long id, LocalDateTime createDate) {
        checkAnswerOwner(id, loginUser);
        Answer answer = findAnswerById(id);
        answer.delete(loginUser, createDate);
        return new AnswerDTO(answer);
    }

    @Transactional
    public QuestionDTO findQuestionAndAnswerById(long questionId) {
        Question question = findQuestionWithAnswer(questionId);
        return new QuestionDTO(question);
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
