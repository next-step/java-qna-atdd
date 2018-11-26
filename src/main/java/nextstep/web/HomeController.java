package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.HttpSessionUtils;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import nextstep.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @Resource(name = "userService")
    private UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        List<Question> questions = StreamSupport
            .stream(qnaService.findAll().spliterator(), false)
            .collect(Collectors.toList());
        log.debug("question size : {}", questions.size());
        model.addAttribute("questions", questions);
        return "home";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        return "user/login";
    }

    @PostMapping("/login")
    public String login(String userId, String password, HttpSession session) throws UnAuthenticationException {
        User loginUser = userService.login(userId, password);
        session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, loginUser);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(@LoginUser User loginUser, HttpSession session) {
        session.removeAttribute(HttpSessionUtils.USER_SESSION_KEY);
        return "redirect:/";
    }
}
