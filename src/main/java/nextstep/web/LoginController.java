package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.security.HttpSessionUtils;
import nextstep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String login() {
        return "/user/login";
    }

    @PostMapping
    public String login(String userId, String password, HttpSession session) throws UnAuthenticationException {
        session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, userService.login(userId, password));
        return "redirect:/questions";
    }
}
