package nextstep.web;

import lombok.RequiredArgsConstructor;
import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import nextstep.security.HttpSessionUtils;
import nextstep.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping
    public String login() {
        return "/user/login";
    }

    @PostMapping
    public String login(String userId, String password, HttpSession httpSession) throws UnAuthenticationException {
        User user = userService.login(userId, password);
        httpSession.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user);
        return "redirect:/";
    }
}
