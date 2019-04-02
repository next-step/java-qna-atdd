package nextstep.web;

import nextstep.domain.Question;
import nextstep.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final QuestionService questionService;

    public HomeController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Question> list = questionService.findAll();

        model.addAttribute("questions", list);
        return "home";
    }
}
