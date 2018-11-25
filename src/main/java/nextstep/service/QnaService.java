package nextstep.service;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import nextstep.CannotDeleteException;
import nextstep.NotFoundException;
import nextstep.UnAuthorizedException;
import nextstep.domain.Answer;
import nextstep.domain.AnswerRepository;
import nextstep.domain.ContentType;
import nextstep.domain.DeleteHistory;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("qnaService")
@Transactional
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
        return questionRepository.save(question);
    }

    public Question updateQuestion(User loginUser, long id, Question question) {
    	Question savedQuestion = findById(id);
    	savedQuestion.update(loginUser, question);
        return savedQuestion;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long id) throws CannotDeleteException {
    	Question savedQuestion = findById(id);
		savedQuestion.delete(loginUser);
		addDeleteHistory(ContentType.QUESTION, id, loginUser);
    }

    public Question findById(long id) {
        return questionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    public Question findByIdAndOwnerAndNotDeleted(User loginUser, long id) {
        return Optional.of(findById(id))
		        .filter(question -> !question.isDeleted())
                .filter(question -> question.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
    	Question question = findById(questionId);
	    Answer answer = new Answer(loginUser, contents);
	    question.addAnswer(answer);
        return answer;
    }

    public Answer deleteAnswer(User loginUser, long id) throws CannotDeleteException {
    	Answer answer = findAnswerById(id);
    	answer.delete(loginUser);
    	addDeleteHistory(ContentType.ANSWER, id, loginUser);
	    return answer;
    }

	public Answer findAnswerById(long id) {
		return answerRepository.findById(id)
				.orElseThrow(NotFoundException::new);
	}

	private void addDeleteHistory(ContentType contentType, long id, User loginUser) {
		DeleteHistory deleteHistory = new DeleteHistory(contentType, id, loginUser);
		deleteHistoryService.saveAll(asList(deleteHistory));
	}
}
