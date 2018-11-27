package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/{id}")
    public String get(@PathVariable long id, Model model) {
        Question question = qnaService.findById(id).get();
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) {
        Question created = qnaService.create(loginUser, question);
        return "redirect:/questions/" + created.getId();
    }
}
