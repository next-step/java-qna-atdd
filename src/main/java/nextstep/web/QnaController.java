package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.dto.QuestionDTO;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questions")
public class QnaController {
    private QnaService qnaService;

    public QnaController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("/form")
    public String createForm(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @PostMapping("/register")
    public String registerQuestion(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return "redirect:/";
    }

    @GetMapping("/show/{id}")
    public String showQuestion(@PathVariable("id") long questionId, Model model) {
//        Question question = qnaService.findById(questionId);
        QuestionDTO questionDTO = qnaService.findQuestionAndAnswerById(questionId);
        model.addAttribute("question", questionDTO);
        model.addAttribute("answersSize", questionDTO.getAnswerSize());

        return "/qna/show";
    }

    @GetMapping("/update/{id}/form")
    public String updateForm(@PathVariable("id") long questionId, @LoginUser User loginUser, Model model) {
        model.addAttribute("question", qnaService.findById(questionId));

        return "/qna/updateForm";
    }

    @PostMapping("/update/{id}")
    public String updateQuestion(@PathVariable("id") long questionId, @LoginUser User loginUser, Question question) {
        qnaService.update(loginUser, questionId, question);
        return "redirect:/";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable("id") long questionId, @LoginUser User loginUser) {
        qnaService.deleteQuestion(loginUser, questionId);
        return "redirect:/";
    }

    @PostMapping("/{id}/answer/add")
    public String addAnswer(@PathVariable("id") long questionId, @LoginUser User loginUser, String contents) {
        qnaService.addAnswer(loginUser, questionId, contents);
        return "redirect:/questions/show/" + questionId;
    }

    @DeleteMapping("/{id}/answer/delete/{answerId}")
    public String deleteAnswer(@PathVariable("id") long questionId, @LoginUser User loginUser, @PathVariable("answerId") long answerId) {
        qnaService.deleteAnswer(loginUser, answerId);
        return "redirect:/questions/show/" + questionId;
    }
}
