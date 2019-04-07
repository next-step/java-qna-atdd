package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.domain.entity.User;
import nextstep.security.HttpSessionUtils;
import nextstep.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Resource(name = "userService")
    private UserService userService;

    @GetMapping("")
    public String loginForm() {
        return "/user/login";
    }

    @PostMapping("")
    public String login(String userId, String password, HttpSession httpSession) {
        try {
            User user = userService.login(userId, password);
            httpSession.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user);
        } catch (UnAuthenticationException e) {
            return "/user/login_failed";
        }
        return "redirect:/users";
    }
}
