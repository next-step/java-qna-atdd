package nextstep.web;

import nextstep.domain.Question;
import nextstep.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static nextstep.security.HttpSessionUtils.USER_SESSION_KEY;

@Controller
public class HomeController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/")
    public String home(Model model) {
        Iterable<Question> questionIterable = qnaService.findAll();
        List<Question> questions = new ArrayList<>();
        questionIterable.iterator()
                .forEachRemaining(questions::add);

        model.addAttribute("questions", questions);
        return "home";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "/user/login";
    }

    @GetMapping("/logout")
    public String doLogout(HttpSession httpSession) {
        httpSession.removeAttribute(USER_SESSION_KEY);
        return "redirect:/";
    }
}
