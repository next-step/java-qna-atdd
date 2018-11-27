package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import nextstep.security.HttpSessionUtils;
import nextstep.security.LoginUser;
import nextstep.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final String USER_UPDATE_VIEW = "/user/updateForm";
    private final String REDIRECT_USERS = "redirect:/users";
    private final String USER_HOME_VIEW = "/user/form";
    private final String USER_LIST_VIEW = "/user/list";
    private final String USER_PROFILE_VIEW = "/user/profile";
    @Resource(name = "userService")
    private UserService userService;

    @GetMapping("/form")
    public String form() {
        return USER_HOME_VIEW;
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
        return USER_LIST_VIEW;
    }

    @GetMapping("/{id}")
    public String showUser(@PathVariable long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return USER_PROFILE_VIEW;
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("user", userService.findById(loginUser, id));
        return USER_UPDATE_VIEW;
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, User target) {
        userService.update(loginUser, id, target);
        return REDIRECT_USERS;
    }

}
