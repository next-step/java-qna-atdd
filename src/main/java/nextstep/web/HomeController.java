package nextstep.web;

import nextstep.domain.Question;
import nextstep.security.HttpSessionUtils;
import nextstep.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/")
    public String home(Model model) {
        List<Question> questions = new ArrayList<>();
        for (Question question : qnaService.findAll()) {
            questions.add(question);
        }
        model.addAttribute("questions", questions);
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "/user/login";
    }
}
