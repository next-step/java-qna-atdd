package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import nextstep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Optional;

@Controller
@RequestMapping("/questions")
public class QnaController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String writeQuestionForm(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @PostMapping("/form")
    public String create(@LoginUser User loginUser, Question question) {
         qnaService.create(loginUser, question);

        return "redirect:/form";
    }

    @GetMapping("/{id}")
    public String questionsShow(@PathVariable Long id, Model model) {
        model.addAttribute("question",qnaService.findById(id));
        return "/qna/show";
    }



}
