package nextstep.web;

import nextstep.domain.User;
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
    public static final String ROUTE_USER_FORM = "/user/form";
    public static final String ROUTE_REDIRECT_USERS = "redirect:/users";
    public static final String ROUTE_USER_LIST = "/user/list";
    public static final String ROUTE_USER_UPDATE_FORM = "/user/updateForm";
    public static final String ATTRIBUTE_USERS = "users";
    public static final String ATTRIBUTE_USER = "user";

    @Resource(name = "userService")
    private UserService userService;

    @GetMapping("/form")
    public String form() {
        return ROUTE_USER_FORM;
    }

    @PostMapping("")
    public String create(User user) {
        userService.add(user);
        return ROUTE_REDIRECT_USERS;
    }

    @GetMapping("")
    public String list(Model model) {
        List<User> users = userService.findAll();
        log.debug("user size : {}", users.size());
        model.addAttribute(ATTRIBUTE_USERS, users);
        return ROUTE_USER_LIST;
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute(ATTRIBUTE_USER, userService.findById(loginUser, id));
        return ROUTE_USER_UPDATE_FORM;
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, User target) {
        userService.update(loginUser, id, target);
        return ROUTE_REDIRECT_USERS;
    }

}
