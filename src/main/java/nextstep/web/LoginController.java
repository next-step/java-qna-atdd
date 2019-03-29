package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import nextstep.security.HttpSessionUtils;
import nextstep.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(String userId, String password, HttpSession httpSession) throws UnAuthenticationException {
        User loginUser = userService.login(userId, password);
        httpSession.setAttribute(HttpSessionUtils.USER_SESSION_KEY, loginUser);

        if (loginUser != null) {
            return "redirect:/users";
        }

        return "/user/login_failed";
    }
}
