package nextstep.web;

import nextstep.domain.Question;
import nextstep.service.QnAService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final QnAService qnAService;

    public HomeController(QnAService qnAService) {
        this.qnAService = qnAService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Question> list = qnAService.findAll();

        model.addAttribute("questions", list);
        return "home";
    }
}
