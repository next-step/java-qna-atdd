package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.dto.AnswerDTO;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by hspark on 27/11/2018.
 */

@RestController
@RequestMapping("/api/answers")
public class ApiAnswerController {
	private final QnaService qnaService;

	public ApiAnswerController(QnaService qnaService) {
		this.qnaService = qnaService;
	}

	@GetMapping("/{answerId}")
	public Answer updateAnswer(@PathVariable long answerId, @LoginUser User loginUser) {
		return qnaService.findAnswer(answerId);
	}

	@PutMapping("/{answerId}")
	public Answer updateAnswer(@PathVariable long answerId, @RequestBody AnswerDTO answerDTO, @LoginUser User loginUser) {
		return qnaService.updateAnswer(loginUser, answerId, answerDTO.getContents());
	}

	@DeleteMapping("/{answerId}")
	public ResponseEntity<Void> deleteAnswer(@PathVariable long answerId, @LoginUser User loginUser)
		throws CannotDeleteException {
		qnaService.deleteAnswer(loginUser, answerId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
