package nextstep.web;

import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class ApiUserController {

    @Resource(name = "userService")
    private UserService userService;

    @PostMapping("")
    public final ResponseEntity<Void> create(@Valid @RequestBody final User user) {

        final User savedUser = userService.add(user);

        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/users/" + savedUser.getId()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public final User detail(@LoginUser final User loginUser,
                       @PathVariable final long id) {
        return userService.findById(loginUser, id);
    }

    @PutMapping("{id}")
    public final User update(@LoginUser final User loginUser,
                       @PathVariable final long id,
                       @Valid @RequestBody final User updatedUser) {
        return userService.update(loginUser, id, updatedUser);
    }

}
