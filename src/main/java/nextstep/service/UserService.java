package nextstep.service;

import lombok.RequiredArgsConstructor;
import nextstep.domain.User;
import nextstep.domain.UserRepository;
import nextstep.exception.UnAuthenticationException;
import nextstep.exception.UnAuthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("userService")
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User add(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User update(User loginUser, long id, User updatedUser) {
        User original = findById(loginUser, id);
        original.update(loginUser, updatedUser);
        return original;
    }

    public User findById(User loginUser, long id) {
        return userRepository.findById(id)
            .filter(user -> user.equals(loginUser))
            .orElseThrow(UnAuthorizedException::new);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User login(String userId, String password) throws UnAuthenticationException {
        return userRepository.findByUserId(userId)
            .filter(user -> user.matchPassword(password))
            .orElseThrow(UnAuthenticationException::new);
    }
}
