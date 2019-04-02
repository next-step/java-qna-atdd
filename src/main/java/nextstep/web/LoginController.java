package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import nextstep.security.HttpSessionUtils;
import nextstep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(String userId, String password, HttpSession session) throws UnAuthenticationException {
        User loginUser = userService.login(userId, password);
        session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, loginUser);
        return "redirect:/";
    }
}
