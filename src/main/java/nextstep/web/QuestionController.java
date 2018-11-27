package nextstep.web;

import javax.annotation.Resource;
import nextstep.AlreadyDeletedException;
import nextstep.CannotDeleteException;
import nextstep.NotFoundException;
import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/questions")
public class QuestionController {

	private static final String REDIRECT_TO_LIST = "redirect:/";
	private static final String HTML_QNA_FORM = "/qna/form";
	private static final String HTML_QNA_SHOW = "/qna/show";
	private static final String HTML_QNA_UPDATE_FORM = "/qna/updateForm";

	@Resource(name = "qnaService")
	private QnaService qnaService;

	@GetMapping("/form")
	public String form(@LoginUser User loginUser) {
		return HTML_QNA_FORM;
	}

	@PostMapping
	public String create(@LoginUser User loginUser, Question question, Model model) {
		model.addAttribute("question", qnaService.createQuestion(loginUser, question));
		return HTML_QNA_SHOW;
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable long id, Model model) {
		model.addAttribute("question", qnaService.findById(id));
		return HTML_QNA_SHOW;
	}

	@GetMapping("/{id}/form")
	public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
		model.addAttribute("question", qnaService.findByIdAndOwnerAndNotDeleted(loginUser, id));
		return HTML_QNA_UPDATE_FORM;
	}

	@PutMapping("/{id}")
	public String update(@LoginUser User loginUser, @PathVariable long id, Question question, Model model) {
		Question savedQuestion = qnaService.updateQuestion(loginUser, id, question);
		model.addAttribute("question", savedQuestion);
		return HTML_QNA_SHOW;
	}

	@DeleteMapping("/{id}")
	public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
		qnaService.deleteQuestion(loginUser, id);
		return REDIRECT_TO_LIST;
	}

	@PostMapping("/{questionId}/answers")
	public String addAnswer(@LoginUser User loginUser, @PathVariable long questionId, Answer answer, Model model) {
		qnaService.addAnswer(loginUser, questionId, answer);
		model.addAttribute("question", qnaService.findById(questionId));
		return HTML_QNA_SHOW;
	}

	@DeleteMapping("/{questionId}/answers/{id}")
	public String deleteAnswer(@LoginUser User loginUser, @PathVariable long questionId,
			@PathVariable long id, Model model) {
		qnaService.deleteAnswer(loginUser, id);
		model.addAttribute("question", qnaService.findById(questionId));
		return HTML_QNA_SHOW;
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleException() {
		return REDIRECT_TO_LIST;
	}

	@ExceptionHandler(AlreadyDeletedException.class)
	@ResponseStatus(HttpStatus.GONE)
	public String handleAlreadyDeletedException() {
		return REDIRECT_TO_LIST;
	}

	@ExceptionHandler(CannotDeleteException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public String handleCannotDeleteException() {
		return REDIRECT_TO_LIST;
	}
}
