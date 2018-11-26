package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

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

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findByIdAndDeletedFalse(id);
    }

	public Optional<Question> findByIdAndUser(long id, User user) {
		return questionRepository.findByIdAndWriterAndDeletedFalse(id, user);
	}

	public Question update(User loginUser, long id, Question updatedQuestion)  {
		Question question = questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		question.update(loginUser,updatedQuestion);
		return question;
	}

	public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
		Question targetQuestion = questionRepository.findByIdAndDeletedFalse(questionId)
			.orElseThrow(() -> new CannotDeleteException("지울 대상이 없습니다."));
		targetQuestion.delete(loginUser);
	}

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }


	public Answer addAnswer(User loginUser, long questionId, String contents) {
		Question question = questionRepository.findByIdAndDeletedFalse(questionId).orElseThrow(IllegalArgumentException::new);
		Answer answer = new Answer(loginUser, contents);
		question.addAnswer(answer);
		return answer;
	}

	public void deleteAnswer(User loginUser, long id) throws CannotDeleteException {
		Answer targetAnswer = answerRepository.findById(id).filter(answer -> !answer.isDeleted()).orElseThrow(
			() -> new CannotDeleteException("지울 대상이 없습니다."));
		targetAnswer.delete(loginUser);
	}

	public Answer updateAnswer(User loginUser, long id, String updateContents) {
		Answer targetAnswer = answerRepository.findById(id).filter(answer -> !answer.isDeleted()).orElseThrow(EntityNotFoundException::new);
		targetAnswer.update(loginUser, updateContents);
		return targetAnswer;
	}

	public Answer findAnswer(long answerId) {
		return answerRepository.findByIdAndDeletedFalse(answerId).orElseThrow(EntityNotFoundException::new);
	}
}
