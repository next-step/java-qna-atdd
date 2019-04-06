package nextstep.web;

import lombok.AllArgsConstructor;
import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import nextstep.security.HttpSessionUtils;
import nextstep.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
@AllArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping
    public String login() {
        return "/user/login";
    }

    @PostMapping
    public String login(@ModelAttribute User user, HttpSession session) throws UnAuthenticationException {
        session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, userService.login(user));
        return "redirect:/questions";
    }
}
