package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import nextstep.web.dto.AnswerDTO;
import nextstep.web.dto.QuestionDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

import static nextstep.web.HomeController.*;

/**
 * Created by hspark on 23/11/2018.
 */

@Controller
@RequestMapping("/questions")
public class QuestionController {
	public static final String QUESTION_DETAIL_VIEW_PATH = "/qna/show";
	public static final String QNA_FORM_VIEW_PATH = "/qna/form";

	private final QnaService qnaService;

	public QuestionController(QnaService qnaService) {
		this.qnaService = qnaService;
	}

	@GetMapping("/form")
	public String getQuestionForm(@LoginUser User login) {
		return QNA_FORM_VIEW_PATH;
	}

	@GetMapping("/{questionId}/form")
	public String getUpdateForm(@PathVariable long questionId, @LoginUser User user, Model model) {
		Question question = qnaService.findByIdAndUser(questionId, user).orElseThrow(EntityNotFoundException::new);
		model.addAttribute("question", question);
		return "/qna/updateForm";
	}

	@GetMapping("/{questionId}")
	public String getQuestion(@PathVariable long questionId, Model model) {
		Question question = qnaService.findById(questionId).orElseThrow(EntityNotFoundException::new);
		model.addAttribute("question", question);
		return QUESTION_DETAIL_VIEW_PATH;
	}

	@PostMapping
	public String addQuestion(QuestionDTO questionDTO, @LoginUser User loginUser, Model model) {
		Question question = Question.of(questionDTO);
		model.addAttribute("question", qnaService.create(loginUser, question));
		return "redirect:/";
	}

	@PutMapping("/{questionId}")
	public String updateQuestion(QuestionDTO updateDTO, @PathVariable long questionId, @LoginUser User loginUser, Model model) {
		Question updateQuestion = Question.of(updateDTO);
		Question question = qnaService.update(loginUser, questionId, updateQuestion);
		model.addAttribute("question", question);
		return QUESTION_DETAIL_VIEW_PATH;
	}

	@DeleteMapping("/{questionId}")
	public String deleteQuestion(@PathVariable long questionId, @LoginUser User loginUser) throws CannotDeleteException {
		qnaService.deleteQuestion(loginUser, questionId);
		return HOME_REDIRECT;
	}

	@PostMapping("/{questionId}/answers")
	public String addAnswer(@PathVariable long questionId, AnswerDTO answerDTO, @LoginUser User loginUser, Model model) {
		qnaService.addAnswer(loginUser, questionId, answerDTO.getContents());
		Question question = qnaService.findById(questionId).orElseThrow(EntityNotFoundException::new);
		model.addAttribute("question", question);
		return QUESTION_DETAIL_VIEW_PATH;
	}

	@DeleteMapping("/{questionId}/answers/{answerId}")
	public String deleteAnswer(@PathVariable long questionId, @PathVariable long answerId, @LoginUser User loginUser, Model model)
		throws CannotDeleteException {
		qnaService.deleteAnswer(loginUser, answerId);
		Question question = qnaService.findById(questionId).orElseThrow(EntityNotFoundException::new);
		model.addAttribute("question", question);
		return QUESTION_DETAIL_VIEW_PATH;
	}

}
