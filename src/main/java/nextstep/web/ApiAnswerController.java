package nextstep.web;

import java.net.URI;
import javax.annotation.Resource;
import javax.validation.Valid;
import nextstep.CannotDeleteException;
import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {

	private static final String API_LINK_PREFIX = "/api";

	@Resource(name = "qnaService")
	private QnaService qnaService;

	@PostMapping
	public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionId, @Valid @RequestBody Answer answer) {
		Answer savedAnswer = qnaService.addAnswer(loginUser, questionId, answer);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(API_LINK_PREFIX + savedAnswer.generateUrl()));
		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	@GetMapping("{id}")
	public Answer show(@PathVariable long id) {
		return qnaService.findAnswerById(id);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
		qnaService.deleteAnswer(loginUser, id);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/"));
		return new ResponseEntity<>(headers, HttpStatus.OK);
	}
}
