package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.security.HttpSessionUtils;
import nextstep.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final String REDIRECT_QUESTIONS = "redirect:/questions";
    private final String LOGIN_FAIL_VIEW = "/user/login_failed";
    private final String REDIRECT_USERS = "redirect:/users";
    private final String LOGIN_VIEW = "/user/login";

    @Resource(name = "userService")
    private UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return LOGIN_VIEW;
    }

    @PostMapping("/login")
    public String login(String userId, String password, HttpSession httpSession) throws UnAuthenticationException {
        if(Optional.ofNullable(userService.login(userId,password)).isPresent()){
            httpSession.setAttribute(HttpSessionUtils.USER_SESSION_KEY, userService.login(userId,password));
            return REDIRECT_QUESTIONS;
        }
        return LOGIN_FAIL_VIEW;
    }


    @GetMapping("/logout")
    public String logout(String userId, String password, HttpSession httpSession) throws UnAuthenticationException {
        httpSession.invalidate();
        return REDIRECT_USERS;
    }
}
