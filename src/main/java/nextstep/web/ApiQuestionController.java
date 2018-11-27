package nextstep.web;


import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
	@javax.annotation.Resource(name ="qnaService")
	private QnaService qnaService;
	
	@PostMapping("")
	public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question question) {
		Question savedQuestion = qnaService.create(loginUser, question);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/api/questions/"+savedQuestion.getId()));
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public Question find(@LoginUser User loginUser, @PathVariable long id) {
		return qnaService.findById(id).orElseThrow(IllegalArgumentException::new);
	}

	@PutMapping("/{id}")
	public Question update(@LoginUser User loginUser, @PathVariable long id, @RequestBody Question updateQuestion) throws CannotUpdateException {
		return qnaService.update(loginUser, id, updateQuestion);
	}

	@DeleteMapping("/{id}")
	public void delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
		qnaService.deleteQuestion(loginUser, id);
	}


	@PostMapping("/{questionId}/answers")
	public ResponseEntity<Void> createAnswer(@LoginUser User loginUser, @PathVariable long questionId, String contents) {
		Answer answer = qnaService.addAnswer(loginUser, questionId, contents);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/api/questions/"+questionId+"/answers/"+answer.getId()));
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}

	@DeleteMapping("/{questionId}/answers/{id}")
	public void deleteAnswer(@LoginUser User loginUser,@PathVariable long questionId,@PathVariable long id) throws CannotDeleteException {
		qnaService.deleteAnswer(loginUser, id);
	}
}
