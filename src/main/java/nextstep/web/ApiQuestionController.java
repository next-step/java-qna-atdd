package nextstep.web;

import java.net.URI;
import javax.annotation.Resource;
import javax.validation.Valid;
import nextstep.CannotDeleteException;
import nextstep.domain.Question;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

	private static final String API_LINK_PREFIX = "/api";

	@Resource(name = "qnaService")
	private QnaService qnaService;

	@PostMapping
	public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question question) {
		Question savedQuestion = qnaService.createQuestion(loginUser, question);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(API_LINK_PREFIX + savedQuestion.generateUrl()));
		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	@GetMapping("{id}")
	public Question show(@PathVariable long id) {
		return qnaService.findById(id);
	}

	@GetMapping
	public Iterable<Question> showAll() {
		return qnaService.findAll();
	}

	@PutMapping("{id}")
	public Question update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Question updateQuestion) {
		return qnaService.updateQuestion(loginUser, id, updateQuestion);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
		qnaService.deleteQuestion(loginUser, id);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/"));
		return new ResponseEntity<>(headers, HttpStatus.OK);
	}
}
