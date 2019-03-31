package nextstep.web;

import nextstep.UnAuthorizedException;
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

    private static final String REFERER = "Referer";
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

    @GetMapping("/{id}")
    public String profile(@LoginUser User loginUser, @PathVariable long id, Model model) {
        if (addCurrentUserToModel(loginUser, id, model)) {
            return "/user/profile";
        }
        return "redirect:/login";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        if (addCurrentUserToModel(loginUser, id, model)) {
            return "/user/updateForm";
        }
        return "redirect:/login";
    }

    private boolean addCurrentUserToModel(@LoginUser User loginUser, @PathVariable long id, Model model) {
        try {
            model.addAttribute("user", userService.findById(loginUser, id));
        } catch (UnAuthorizedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, User target) {
        userService.update(loginUser, id, target);
        return "redirect:/users";
    }
}
