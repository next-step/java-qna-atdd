package nextstep.web;

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
public class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String loginView() {
        return "/user/login";
    }

    @PostMapping
    public String login(HttpSession session, User user) {
        try {
            User loginUser = userService.login(user.getUserId(), user.getPassword());
            HttpSessionUtils.login(session, loginUser);
        } catch (UnAuthenticationException e) {
            return "/user/login_failed";
        }

        return "redirect:/users";
    }
}
