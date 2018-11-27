package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import nextstep.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Controller
@RequestMapping(value = "/login")
public class LoginController {

    private static final String LOGIN_SUCCESS = "redirect:/users";
    private static final String LOGIN_FAIL = "/users/login_failed";

    @Resource(name = "userService")
    private UserService userService;

    @GetMapping
    public String loginPage() {
        return "/user/login";
    }

    @PostMapping
    public String login(final String userId, final String password, final HttpSession httpSession) throws UnAuthenticationException {
        final Optional<User> loginUser = ofNullable(userService.login(userId, password));
        loginUser.ifPresent(user -> userService.createSession(user, httpSession));
        return loginUser.isPresent() ? LOGIN_SUCCESS : LOGIN_FAIL;
    }

}
