package nextstep.web;

import nextstep.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Resource(name = "questionService")
    private QuestionService questionService;

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @GetMapping("/{id}")
    public String readDetail(@PathVariable long id, Model model) {
        model.addAttribute("question", questionService.findById(id));
        return "/qna/show";
    }
}
