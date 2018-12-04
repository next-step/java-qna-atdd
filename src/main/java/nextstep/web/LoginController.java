package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import nextstep.security.HttpSessionUtils;
import nextstep.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "/user/login";
    }

    @PostMapping("/login")
    public String login(String userId, String password, HttpSession httpSession) throws UnAuthenticationException {
        Optional<User> user = Optional.ofNullable(userService.login(userId, password));

        if(user.isPresent()) {
            httpSession.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user);
            return "redirect:/";
        }

        return "/user/login_failed";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
