package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import nextstep.service.QnaService;
import nextstep.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static nextstep.security.HttpSessionUtils.USER_SESSION_KEY;

@Controller
public class HomeController {

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/")
    public String home(Model model) {
        return "redirect:/questions";
    }

    @GetMapping("/login")
    public String login() {
        return "/user/login";
    }

    @PostMapping("/login")
    public String login(String userId, String password, HttpSession httpSession, HttpServletRequest request) {
        try {
            User user = userService.login(userId, password);
            httpSession.setAttribute(USER_SESSION_KEY, user);
        } catch (UnAuthenticationException e) {
            e.printStackTrace();
            return "/user/login_failed";
        }

        return "redirect:/questions";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute(USER_SESSION_KEY);
        return "redirect:/questions";
    }
}
