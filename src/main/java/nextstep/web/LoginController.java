package nextstep.web;

import javax.servlet.http.HttpSession;
import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import nextstep.security.HttpSessionUtils;
import nextstep.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String loginForm(HttpSession httpSession) {
        User user = HttpSessionUtils.getUserFromSession(httpSession);

        if (user.isGuestUser()) {
            return "/user/login";
        }

        return "redirect:/";
    }

    @PostMapping("")
    public String login(String userId, String password, HttpSession httpSession) {
        try {
            User loginUser = userService.login(userId, password);
            httpSession.setAttribute(HttpSessionUtils.USER_SESSION_KEY, loginUser);
        } catch (UnAuthenticationException e) {
            return "redirect:/login/loginFailed";
        }

        return "redirect:/users";
    }

    @GetMapping("/loginFailed")
    public String loginFailed(HttpSession httpSession) {
        User user = HttpSessionUtils.getUserFromSession(httpSession);

        if (!user.isGuestUser()) {
            return "redirect:/";
        }

        return "/user/loginFailed";
    }
}
