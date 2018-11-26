package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.dto.AnswerDTO;
import nextstep.dto.QuestionDTO;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.List;

/**
 * Created by hspark on 23/11/2018.
 */

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
	public static final String API_PATH = "/api";

	private final QnaService qnaService;

	public ApiQuestionController(QnaService qnaService) {
		this.qnaService = qnaService;
	}

	@GetMapping
	public List<Question> getQuestions(Pageable pageable) {
		return qnaService.findAll(pageable);
	}

	@GetMapping("/{questionId}")
	public Question getQuestion(@PathVariable long questionId) {
		return qnaService.findById(questionId).orElseThrow(EntityNotFoundException::new);
	}

	@PostMapping
	public ResponseEntity<Void> addQuestion(@RequestBody QuestionDTO questionDTO, @LoginUser User loginUser) {
		Question question = Question.of(questionDTO);
		Question savedQuestion = qnaService.create(loginUser, question);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(API_PATH + savedQuestion.generateUrl()));
		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	@PutMapping("/{questionId}")
	public Question updateQuestion(@RequestBody QuestionDTO updateDTO, @PathVariable long questionId, @LoginUser User loginUser) {
		Question updateQuestion = Question.of(updateDTO);
		return qnaService.update(loginUser, questionId, updateQuestion);

	}

	@DeleteMapping("/{questionId}")
	public ResponseEntity<Void> deleteQuestion(@PathVariable long questionId, @LoginUser User loginUser) throws CannotDeleteException {
		qnaService.deleteQuestion(loginUser, questionId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/{questionId}/answers")
	public ResponseEntity<Void> addAnswer(@PathVariable long questionId, @RequestBody AnswerDTO answerDTO, @LoginUser User loginUser) {
		Answer savedAnswer = qnaService.addAnswer(loginUser, questionId, answerDTO.getContents());

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(API_PATH + savedAnswer.generateUrl()));
		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}
}
