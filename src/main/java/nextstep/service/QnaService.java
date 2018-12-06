package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.UnAuthenticationException;
import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        // 새로운 질문 생성하기!
        question.writeBy(loginUser);
        log.debug("question : {}",  question.generateUrl());
        return questionRepository.save(question);
    }

    public Question findByQuestionId(long questionId){
        return questionRepository.findById(questionId).get();
    }
/*    public Optional<Question> findById(long questionId){
        //질문 찾기!
        return questionRepository.findById(questionId);
    }*/


    @Transactional
    public Question update(User loginUser, long questionId, Question updatedQuestion) throws UnAuthenticationException {
        // TODO 수정 기능 구현
       Question original = findByQuestionId(questionId);
       original.update(loginUser, updatedQuestion);
       return original;
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long questionId) throws CannotDeleteException, UnAuthenticationException {
        // TODO 삭제 기능 구현
        Question original = findByQuestionId(questionId);
        original.delete(loginUser);

        DeleteHistory deleteHistory = new DeleteHistory(ContentType.QUESTION, questionId, loginUser, LocalDateTime.now());
        deleteHistoryService.saveOne(deleteHistory);
        return original;
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        // TODO 답변 추가 기능 구현
        Question original = findByQuestionId(questionId);
        Answer newAnswer = new Answer(loginUser, contents);
        original.addAnswer(newAnswer);

        return newAnswer;
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long answerId, String updateContents){
        Answer answer = findByAnswerId(answerId);
        answer.update(loginUser, updateContents);
        return answer;
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long answerId) throws CannotDeleteException, UnAuthenticationException{
        // TODO 답변 삭제 기능 구현
        Answer original =findByAnswerId(answerId);
        original.delete(loginUser);

        DeleteHistory deleteHistory = new DeleteHistory(ContentType.ANSWER, answerId, loginUser, LocalDateTime.now());
        deleteHistoryService.saveOne(deleteHistory);
        return original;
    }
    public Answer findByAnswerId(long answerId){
        return answerRepository.findById(answerId).get();
    }
}
