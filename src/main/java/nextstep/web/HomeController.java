package nextstep.web;

import lombok.RequiredArgsConstructor;
import nextstep.domain.Question;
import nextstep.security.HttpSessionUtils;
import nextstep.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final QnaService qnaService;

    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(required = false, defaultValue = "1") int page,
                       @RequestParam(required = false, defaultValue = "10") int size) {
        List<Question> questions = qnaService.findAll(page, size);
        model.addAttribute("questions", questions);
        return "/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute(HttpSessionUtils.USER_SESSION_KEY);

        return "redirect:/";
    }
}
