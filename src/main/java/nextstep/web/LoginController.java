package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import nextstep.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import static nextstep.security.HttpSessionUtils.USER_SESSION_KEY;

@Controller
public class LoginController {
    @Resource(name = "userService")
    private UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

    @PostMapping("/login")
    public String login(HttpSession session, String userId, String password) throws UnAuthenticationException {
        User user = userService.login(userId, password);
        session.setAttribute(USER_SESSION_KEY, user);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(USER_SESSION_KEY);
        return "redirect:/";
    }
}
