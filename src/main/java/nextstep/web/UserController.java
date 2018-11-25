package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private static final String LOGIN_SUCCESS_PATH = "redirect:/users";
    private static final String LOGIN_FAIL_PATH = "/users/login_failed";

    @Resource(name = "userService")
    private UserService userService;

    @GetMapping("/form")
    public String form() {
        return "/user/form";
    }

    @PostMapping("")
    public String create(User user) {
        userService.add(user);
        return "redirect:/users";
    }

    @GetMapping("")
    public String list(Model model) {
        List<User> users = userService.findAll();
        log.debug("user size : {}", users.size());
        model.addAttribute("users", users);
        return "/user/list";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("user", userService.findById(loginUser, id));
        return "/user/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, User target) {
        userService.update(loginUser, id, target);
        return "redirect:/users";
    }

    @PostMapping("/login")
    public String login(final String userId, final String password, final HttpSession httpSession) throws UnAuthenticationException {
        final Optional<User> loginUser = ofNullable(userService.login(userId, password));
        loginUser.ifPresent(user -> userService.createSession(user, httpSession));
        return loginUser.isPresent() ? LOGIN_SUCCESS_PATH : LOGIN_FAIL_PATH;
    }

}
