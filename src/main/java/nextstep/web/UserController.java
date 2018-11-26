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
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    public static final String USER_FORM = "/user/form";
    public static final String USER_LIST = "/user/list";
    public static final String USER_UPDATE_FORM = "/user/updateForm";
    public static final String REDIRECT = "redirect:/";
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    
    @Resource(name = "userService")
    private UserService userService;

    @GetMapping("/form")
    public String form() {
        return USER_FORM;
    }

    @PostMapping("")
    public String create(User user) {
        userService.add(user);
        return REDIRECT;
    }

    @GetMapping("")
    public String list(Model model) {
        List<User> users = userService.findAll();
        log.debug("user size : {}", users.size());
        model.addAttribute("users", users);
        return USER_LIST;
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("user", userService.findById(loginUser, id));
        return USER_UPDATE_FORM;
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, User target) {
        userService.update(loginUser, id, target);
        return REDIRECT;
    }




}
